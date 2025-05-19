package com.desmin.controllers;

import com.desmin.pojo.ChatRoom;
import com.desmin.pojo.Message;
import com.desmin.pojo.User;
import com.desmin.services.ChatRoomService;
import com.desmin.services.MessageService;
import com.desmin.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private ChatRoomService chatRoomService;

    @Autowired
    private UserService userService;

    @PostMapping(value = "/secure/send", consumes = {"multipart/form-data"})
    public ResponseEntity<?> sendMessage(
            @RequestParam(value = "roomId", required = false) Long roomId,
            @RequestParam("senderId") Long senderId,
            @RequestParam(value = "content", required = false) String content,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            @RequestParam(value = "messageType", defaultValue = "TEXT") String messageType) {
        try {
            User sender = userService.getUserById(senderId);
            if (sender == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Sender không tồn tại với ID: " + senderId);
                return ResponseEntity.badRequest().body(error);
            }

            ChatRoom room;
            if (roomId != null) {
                room = chatRoomService.getChatRoomById(roomId);
                if (room == null) {
                    Map<String, String> error = new HashMap<>();
                    error.put("error", "ChatRoom không tồn tại với ID: " + roomId);
                    return ResponseEntity.badRequest().body(error);
                }
                // Kiểm tra quyền truy cập
                if (sender.getRole() == User.Role.SINH_VIEN) {
                    if (!room.getSinhVien().getId().equals(senderId) || !room.getKhoa().getId().equals(sender.getLop().getKhoa().getId())) {
                        Map<String, String> error = new HashMap<>();
                        error.put("error", "Sinh viên không thuộc ChatRoom này");
                        return ResponseEntity.badRequest().body(error);
                    }
                } else if (sender.getRole() == User.Role.TRO_LY_SINH_VIEN) {
                    if (!sender.getKhoaPhuTrach().getId().equals(room.getKhoa().getId())) {
                        Map<String, String> error = new HashMap<>();
                        error.put("error", "Trợ lý không phụ trách khoa của ChatRoom này");
                        return ResponseEntity.badRequest().body(error);
                    }
                }
            } else {
                // Tự động tạo ChatRoom cho sinh viên nếu chưa có
                if (sender.getRole() == User.Role.SINH_VIEN) {
                    if (sender.getLop() == null || sender.getLop().getKhoa() == null) {
                        Map<String, String> error = new HashMap<>();
                        error.put("error", "Sinh viên không thuộc lớp hoặc khoa nào");
                        return ResponseEntity.badRequest().body(error);
                    }
                    room = chatRoomService.createChatRoom(sender, null);
                } else if (sender.getRole() == User.Role.TRO_LY_SINH_VIEN) {
                    Map<String, String> error = new HashMap<>();
                    error.put("error", "Trợ lý cần roomId để gửi tin nhắn");
                    return ResponseEntity.badRequest().body(error);
                } else {
                    Map<String, String> error = new HashMap<>();
                    error.put("error", "Vai trò không hợp lệ");
                    return ResponseEntity.badRequest().body(error);
                }
            }

            Message message = messageService.sendMessage(room, sender, content, imageFile, messageType, false);
            if (message == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Lỗi khi gửi tin nhắn");
                return ResponseEntity.status(500).body(error);
            }
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Lỗi server: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    @GetMapping("/secure/rooms")
    public ResponseEntity<?> getRooms(@RequestParam("userId") Long userId) {
        try {
            User user = userService.getUserById(userId);
            if (user == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "User không tồn tại với ID: " + userId);
                return ResponseEntity.badRequest().body(error);
            }

            if (user.getRole() == User.Role.SINH_VIEN) {
                List<ChatRoom> rooms = chatRoomService.getChatRoomsByUserId(userId);
                if (rooms.isEmpty()) {
                    ChatRoom room = chatRoomService.createChatRoom(user, null);
                    return ResponseEntity.ok(List.of(room));
                }
                return ResponseEntity.ok(rooms);
            } else if (user.getRole() == User.Role.TRO_LY_SINH_VIEN) {
                if (user.getKhoaPhuTrach() == null) {
                    Map<String, String> error = new HashMap<>();
                    error.put("error", "Trợ lý không phụ trách khoa nào");
                    return ResponseEntity.badRequest().body(error);
                }
                // Logic giả định lấy tất cả ChatRoom của khoa (cần cập nhật repository/service nếu cần)
                List<ChatRoom> rooms = chatRoomService.getChatRoomsByKhoaId(user.getKhoaPhuTrach().getId());
                return ResponseEntity.ok(rooms);
            } else {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Vai trò không hợp lệ");
                return ResponseEntity.badRequest().body(error);
            }
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Lỗi server: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    @GetMapping("/secure/room/{roomId}")
    public ResponseEntity<?> getMessagesByRoom(@PathVariable("roomId") Long roomId) {
        try {
            List<Message> messages = messageService.getMessagesByRoomId(roomId);
            return ResponseEntity.ok(messages);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Lỗi server: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }
}
package com.desmin.services.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.desmin.pojo.ChatRoom;
import com.desmin.pojo.Khoa;
import com.desmin.pojo.Message;
import com.desmin.pojo.User;
import com.desmin.repositories.ChatRoomRepository;
import com.desmin.repositories.MessageRepository;
import com.desmin.services.MessageService;
import com.desmin.services.RasaService;
import com.desmin.services.UserService;
import com.google.firebase.database.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author ADMIN
 */
@Transactional
@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private RasaService rasaService;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private Cloudinary cloudinary;

    @Autowired
    private UserService userService;

    @Override
    public Message sendMessage(ChatRoom room, User sender, String content, MultipartFile imageFile, String messageType, boolean isBot) {
        try {
            // Kiểm tra dữ liệu đầu vào
            if (sender == null || sender.getId() == null) {
                throw new IllegalArgumentException("Sender cannot be null or must have a valid ID");
            }

            // Tìm hoặc tạo ChatRoom
            ChatRoom chatRoomToUse = null;
            Khoa khoa = null;
            if (room != null && room.getId() != null) {
                chatRoomToUse = chatRoomRepository.findById(room.getId());
                if (chatRoomToUse == null) {
                    throw new IllegalArgumentException("ChatRoom with ID " + room.getId() + " not found");
                }
                khoa = chatRoomToUse.getKhoa();
            } else {
                // Xác định khoa từ sender (chỉ khi sender là sinh viên)
                if (sender.getRole() == User.Role.SINH_VIEN) {
                    if (sender.getLop() == null || sender.getLop().getKhoa() == null) {
                        throw new IllegalArgumentException("Sinh viên không thuộc lớp hoặc khoa nào");
                    }
                    khoa = sender.getLop().getKhoa();
                    chatRoomToUse = chatRoomRepository.findByKhoaIdAndSinhVienId(khoa.getId(), sender.getId());
                    if (chatRoomToUse == null) {
                        chatRoomToUse = new ChatRoom();
                        chatRoomToUse.setFirebaseRoomId(UUID.randomUUID().toString());
                        chatRoomToUse.setKhoa(khoa);
                        chatRoomToUse.setSinhVien(sender);
                        chatRoomToUse.setCreatedAt(LocalDateTime.now());
                        chatRoomToUse = chatRoomRepository.save(chatRoomToUse);
                    }
                } else if (sender.getRole() == User.Role.TRO_LY_SINH_VIEN) {
                    // Trợ lý cần roomId để gửi tin nhắn
                    if (room == null || room.getId() == null) {
                        throw new IllegalArgumentException("RoomId phải được cung cấp khi trợ lý gửi tin nhắn");
                    }
                    chatRoomToUse = chatRoomRepository.findById(room.getId());
                    if (chatRoomToUse == null) {
                        throw new IllegalArgumentException("ChatRoom with ID " + room.getId() + " not found");
                    }
                    khoa = chatRoomToUse.getKhoa();
                } else {
                    throw new IllegalArgumentException("Sender must be a SINH_VIEN or TRO_LY");
                }
            }

            // Tạo và lưu tin nhắn
            Message message = new Message();
            message.setRoom(chatRoomToUse);
            message.setSender(sender);
            message.setContent(content);
            message.setMessageType(messageType != null ? messageType : "TEXT");
            message.setBot(isBot);
            message.setStatus("SENT");

            // Xử lý hình ảnh
            if (imageFile != null && !imageFile.isEmpty()) {
                try {
                    Map res = cloudinary.uploader().upload(imageFile.getBytes(),
                            ObjectUtils.asMap("resource_type", "auto"));
                    message.setFileUrl(res.get("secure_url").toString());
                    message.setMessageType("IMAGE");
                } catch (IOException ex) {
                    Logger.getLogger(MessageServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
                    throw new RuntimeException("Lỗi khi upload ảnh tin nhắn: " + ex.getMessage());
                }
            }

            message = messageRepository.save(message);

            // Lưu tin nhắn lên Firebase Realtime Database
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference messagesRef = database.getReference("messages/" + message.getId());
            Map<String, Object> messageData = new HashMap<>();
            messageData.put("content", message.getContent());
            messageData.put("fileUrl", message.getFileUrl());
            messageData.put("messageType", message.getMessageType());
            messageData.put("roomId", chatRoomToUse.getId());
            messageData.put("timestamp", message.getTimestamp()
                    .atZone(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli());
            messageData.put("isBot", message.isBot());
            messageData.put("senderId", sender.getId());
            messagesRef.setValueAsync(messageData);

            // Lưu thông tin ChatRoom lên Firebase
            DatabaseReference chatRoomsRef = database.getReference("chat_rooms/" + chatRoomToUse.getId());
            Map<String, Object> chatRoomData = new HashMap<>();
            chatRoomData.put("firebaseRoomId", chatRoomToUse.getFirebaseRoomId());
            chatRoomData.put("khoaId", chatRoomToUse.getKhoa().getId()); // Lưu khoaId thay vì khoa object
            chatRoomData.put("sinhVienId", chatRoomToUse.getSinhVien().getId());
            chatRoomData.put("createdAt", chatRoomToUse.getCreatedAt()
                    .atZone(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli());
            chatRoomsRef.setValueAsync(chatRoomData);

            // Kiểm tra trạng thái online của tất cả trợ lý trong khoa (chỉ khi sinh viên gửi)
            if (!isBot && sender.getRole() == User.Role.SINH_VIEN && content != null && !content.isEmpty()) {
                List<User> troLys = userService.getTroLysByKhoaId(khoa.getId());
                boolean[] anyTroLyOnline = {false};
                CountDownLatch latch = new CountDownLatch(troLys.size());

                for (User troLy : troLys) {
                    DatabaseReference ref = database.getReference("users/" + troLy.getId() + "/online");
                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            if (snapshot.exists() && snapshot.getValue(Boolean.class) != null && snapshot.getValue(Boolean.class)) {
                                anyTroLyOnline[0] = true;
                            }
                            latch.countDown();
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                            Logger.getLogger(MessageServiceImpl.class.getName()).log(Level.SEVERE, "Firebase error", error.toException());
                            latch.countDown();
                        }
                    });
                }

                try {
                    latch.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    Logger.getLogger(MessageServiceImpl.class.getName()).log(Level.SEVERE, "Interrupted while waiting for Firebase", e);
                }

                if (!anyTroLyOnline[0]) {
                    String botResponse = rasaService.getRasaResponse(content, sender.getId().toString());
                    Message botMessage = new Message(chatRoomToUse, null, botResponse, null, "TEXT", true);
                    botMessage = messageRepository.save(botMessage);

                    DatabaseReference botMessagesRef = database.getReference("messages/" + botMessage.getId());
                    Map<String, Object> botMessageData = new HashMap<>();
                    botMessageData.put("content", botMessage.getContent());
                    botMessageData.put("fileUrl", botMessage.getFileUrl());
                    botMessageData.put("messageType", botMessage.getMessageType());
                    botMessageData.put("roomId", chatRoomToUse.getId());
                    botMessageData.put("timestamp", botMessage.getTimestamp()
                            .atZone(ZoneId.systemDefault())
                            .toInstant()
                            .toEpochMilli());
                    botMessageData.put("isBot", botMessage.isBot());
                    botMessagesRef.setValueAsync(botMessageData);
                }
            }

            return message;
        } catch (Exception ex) {
            Logger.getLogger(MessageServiceImpl.class.getName()).log(Level.SEVERE, "Error when sending message", ex);
            throw new RuntimeException("Lỗi khi gửi tin nhắn: " + ex.getMessage(), ex);
        }
    }

    @Override
    public List<Message> getMessagesByRoomId(Long roomId) {
        return messageRepository.findByRoomId(roomId);
    }

}

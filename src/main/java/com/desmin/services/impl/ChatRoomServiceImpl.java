package com.desmin.services.impl;

import com.desmin.pojo.ChatRoom;
import com.desmin.pojo.Khoa;
import com.desmin.pojo.User;
import com.desmin.repositories.ChatRoomRepository;
import com.desmin.services.ChatRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ChatRoomServiceImpl implements ChatRoomService {

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Override
    public ChatRoom createChatRoom(User sinhVien, String firebaseRoomId) {
        if (sinhVien == null || sinhVien.getId() == null) {
            throw new IllegalArgumentException("Sinh viên không được null và phải có ID hợp lệ");
        }
        if (sinhVien.getRole() != User.Role.SINH_VIEN) {
            throw new IllegalArgumentException("User phải là SINH_VIEN");
        }
        if (sinhVien.getLop() == null || sinhVien.getLop().getKhoa() == null) {
            throw new IllegalArgumentException("Sinh viên không thuộc lớp hoặc khoa nào");
        }

        Khoa khoa = sinhVien.getLop().getKhoa();
        // Tìm ChatRoom dựa trên khoaId và sinhVienId
        ChatRoom existingRoom = chatRoomRepository.findByKhoaIdAndSinhVienId(khoa.getId(), sinhVien.getId());
        if (existingRoom != null) {
            return existingRoom;
        }

        // Tạo ChatRoom mới
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setFirebaseRoomId(firebaseRoomId != null ? firebaseRoomId : UUID.randomUUID().toString());
        chatRoom.setSinhVien(sinhVien);
        chatRoom.setKhoa(khoa);
        chatRoom.setCreatedAt(LocalDateTime.now());
        return chatRoomRepository.save(chatRoom);
    }

    @Override
    public List<ChatRoom> getChatRoomsByUserId(Long userId) {
        return chatRoomRepository.findByParticipantId(userId);
    }

    @Override
    public ChatRoom getChatRoomById(Long id) {
        return chatRoomRepository.findById(id);
    }

    @Override
    public List<ChatRoom> getChatRoomsByKhoaId(Long khoaId) {
                return chatRoomRepository.getChatRoomsByKhoaId(khoaId);

    }
}
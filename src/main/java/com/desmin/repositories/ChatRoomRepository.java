/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.desmin.repositories;

import com.desmin.pojo.ChatRoom;
import java.util.List;

/**
 *
 * @author ADMIN
 */

public interface ChatRoomRepository {
    ChatRoom save(ChatRoom chatRoom);
    List<ChatRoom> findByParticipantId(Long userId);
    ChatRoom findById(Long id);
    ChatRoom findByKhoaIdAndSinhVienId(Long khoaId, Long sinhVienId);
    List<ChatRoom> getChatRoomsByKhoaId(Long khoaId);
}
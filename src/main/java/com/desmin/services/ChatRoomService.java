/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.desmin.services;

import com.desmin.pojo.ChatRoom;
import com.desmin.pojo.User;
import java.util.List;

/**
 *
 * @author ADMIN
 */
public interface ChatRoomService {
    ChatRoom createChatRoom(User sinhVien, String firebaseRoomId);
    List<ChatRoom> getChatRoomsByUserId(Long userId);
    ChatRoom getChatRoomById(Long id);
    List<ChatRoom> getChatRoomsByKhoaId(Long khoaId);
}
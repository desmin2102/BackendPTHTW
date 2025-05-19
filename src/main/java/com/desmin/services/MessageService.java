/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.desmin.services;

import com.desmin.pojo.ChatRoom;
import com.desmin.pojo.Message;
import com.desmin.pojo.User;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author ADMIN
 */
public interface MessageService {
    Message sendMessage(ChatRoom room, User sender, String content, MultipartFile imageFile, String messageType, boolean isBot);
    List<Message> getMessagesByRoomId(Long roomId);
}
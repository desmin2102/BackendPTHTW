/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.desmin.repositories;

import com.desmin.pojo.Message;
import java.util.List;

/**
 *
 * @author ADMIN
 */

public interface MessageRepository {
    Message save(Message message);
    List<Message> findByRoomId(Long roomId);
}
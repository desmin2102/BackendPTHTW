/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.desmin.repositories;

import com.desmin.pojo.User;
import java.util.List;
import java.util.Map;

/**
 *
 * @author ADMIN
 */
public interface UserRepository {

    User getUserByUsername(String username);

    User addUser(User tk);

    boolean authenticate(String username, String password);

    User getUserById(long id);

    List<User> findAllSinhVien();
    
        List<User> getAllSinhVien(Map<String, String> params);


}

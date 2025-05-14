/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.desmin.services;

import com.desmin.pojo.Khoa;
import com.desmin.pojo.User;
import java.util.Map;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author admin
 */
public interface UserService extends UserDetailsService {

    User getUserByUsername(String username);

    User getUserById(long id);

    User addSinhVien(Map<String, String> params, MultipartFile avatar);

    User addTroLySinhVien(Map<String, String> params, MultipartFile avatar);

    User addCVCTSV(Map<String, String> params, MultipartFile avatar);

    boolean authenticate(String username, String password);
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.desmin.controllers;

import com.desmin.pojo.User;
import com.desmin.services.UserService;
import com.desmin.utils.JwtUtils;
import jakarta.annotation.PostConstruct;
import java.security.Principal;
import java.util.Collections;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping; // Thêm import cho endpoint test
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author admin
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class ApiUserController {

    @Autowired
    private UserService userDetailsService;


    @PostMapping(path = "/users", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> create(@RequestParam Map<String, String> params,
                                   @RequestParam(value = "avatar", required = false) MultipartFile avatar,
                                   Principal principal) {
        try {
            System.out.println("Creating user with params: " + params);
            if (params == null || params.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Thiếu thông tin người dùng");
            }

            String roleStr = params.getOrDefault("role", "SINH_VIEN");

            if ("TRO_LY_SINH_VIEN".equals(roleStr)) {
                if (principal == null) {
                    System.out.println("Unauthorized: No principal provided for creating TRO_LY_SINH_VIEN");
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Cần đăng nhập để tạo trợ lý sinh viên");
                }
                User currentUser = userDetailsService.getUserByUsername(principal.getName());
                if (currentUser == null) {
                    System.out.println("User not found for principal: " + principal.getName());
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Người dùng hiện tại không tồn tại");
                }
                if (!"CVCTSV".equals(currentUser.getRole().name())) {
                    System.out.println("Forbidden: User " + principal.getName() + " is not CVCTSV");
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Bạn không có quyền tạo trợ lý sinh viên");
                }
                User createdUser = this.userDetailsService.addTroLySinhVien(params, avatar);
                return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
            } else {
                User createdUser = this.userDetailsService.addSinhVien(params, avatar);
                return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
            }
        } catch (Exception e) {
            System.err.println("Error creating user: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi tạo người dùng: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User u) {
    
            if (this.userDetailsService.authenticate(u.getUsername(), u.getPassword())) {
                try {
                    String token = JwtUtils.generateToken(u.getUsername());
                    System.out.println("Login successful for username: " + u.getUsername());
                    return ResponseEntity.ok().body(Collections.singletonMap("token", token));
                } catch (Exception e) {
                    System.err.println("Error generating JWT for username: " + u.getUsername() + ", error: " + e.getMessage());
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi tạo JWT: " + e.getMessage());
                }
            }
            System.out.println("Login failed for username: " + u.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Sai thông tin đăng nhập");
      
    }

    @GetMapping("/secure/profile")
    public ResponseEntity<?> getProfile(Principal principal) {
        try {
            if (principal == null) {
                System.out.println("Unauthorized: No principal provided for profile access");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Cần đăng nhập để xem hồ sơ");
            }
            String username = principal.getName();
            System.out.println("Fetching profile for username: " + username);
            User user = this.userDetailsService.getUserByUsername(username);
            if (user == null) {
                System.out.println("User not found for username: " + username);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy người dùng");
            }
            System.out.println("Profile fetched successfully for username: " + username);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println("Error fetching profile: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi lấy hồ sơ: " + e.getMessage());
        }
    }

  
}
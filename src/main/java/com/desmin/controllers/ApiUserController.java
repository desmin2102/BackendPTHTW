/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.desmin.controllers;

import com.desmin.pojo.User;
import com.desmin.services.UserService;
import com.desmin.utils.JwtUtils;
import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping; // Thêm import cho endpoint test
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
//    @Autowired
//    private HoatDongNgoaiKhoaService hdnkService;

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

            User createdUser = this.userDetailsService.addSinhVien(params, avatar);
            return new ResponseEntity<>(createdUser, HttpStatus.CREATED);

        } catch (Exception e) {
            System.err.println("Error creating user: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi tạo người dùng: " + e.getMessage());
        }
    }

@PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User u) {
        if (userDetailsService.authenticate(u.getUsername(), u.getPassword())) {
            try {
                // Lấy UserDetails để trích xuất vai trò
                UserDetails userDetails = userDetailsService.loadUserByUsername(u.getUsername());
                List<String> roles = userDetails.getAuthorities().stream()
                        .map(authority -> authority.getAuthority())
                        .collect(Collectors.toList());

                // Tạo token với username và roles
                String token = JwtUtils.generateToken(u.getUsername(), roles);
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

    @PostMapping(path = "/secure/tlsv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createTLSV(@RequestParam Map<String, String> params,
            @RequestParam(value = "avatar", required = false) MultipartFile avatar,
            Principal principal) {
        try {
            System.out.println("Creating TRO_LY_SINH_VIEN with params: " + params);
            if (params == null || params.isEmpty()) {
                System.out.println("Bad Request: Missing or empty params");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Thiếu thông tin người dùng");
            }
            if (principal == null) {
                System.out.println("Unauthorized: No principal provided for creating TRO_LY_SINH_VIEN");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Cần đăng nhập để tạo trợ lý sinh viên");
            }
            String username = principal.getName();
            System.out.println("Fetching user for username: " + username);
            User currentUser = userDetailsService.getUserByUsername(username);
            if (currentUser == null) {
                System.out.println("User not found for username: " + username);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Người dùng hiện tại không tồn tại");
            }
            if (!"CVCTSV".equals(currentUser.getRole().name())) {
                System.out.println("Forbidden: User " + username + " is not CVCTSV, role: " + currentUser.getRole().name());
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Bạn không có quyền tạo trợ lý sinh viên");
            }
            System.out.println("User " + username + " authorized with role CVCTSV, proceeding to create TRO_LY_SINH_VIEN");
            User createdUser = userDetailsService.addTroLySinhVien(params, avatar);
            System.out.println("TRO_LY_SINH_VIEN created successfully for username: " + createdUser.getUsername());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (Exception e) {
            System.err.println("Error creating TRO_LY_SINH_VIEN: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi tạo trợ lý sinh viên: " + e.getMessage());
        }
    }

//    @GetMapping("/users/{userId}/hoat-dong")
//    public ResponseEntity<List<HoatDongNgoaiKhoa>> getUserRegisteredOrAttendedActivities(@PathVariable(value = "userId") long userId) {
//        User user = userDetailsService.getUserById(userId);
//        if (user == null || user.getRole() != User.Role.SINH_VIEN) {
//            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
//        }
//        return new ResponseEntity<>(hdnkService.findByUserRegisteredOrAttended(user), HttpStatus.OK);
//    }

}

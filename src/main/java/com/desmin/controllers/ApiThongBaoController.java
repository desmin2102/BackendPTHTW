/*
 * Click nb://fs://SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nb://fs://SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.desmin.controllers;

import com.desmin.pojo.ThongBao;
import com.desmin.pojo.User;
import com.desmin.services.ThongBaoService;
import com.desmin.services.UserService;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.security.Principal;
import org.springframework.web.bind.annotation.PutMapping;

/**
 *
 * @author ADMIN
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class ApiThongBaoController {

    @Autowired
    private ThongBaoService thongBaoService;

    @Autowired
    private UserService userService;

    @GetMapping("/secure/thong-bao")
    public ResponseEntity<List<ThongBao>> list(@RequestParam Map<String, String> params) {
        return new ResponseEntity<>(this.thongBaoService.getThongBaos(params), HttpStatus.OK);
    }

    @GetMapping("/secure/thong-bao/{thongBaoId}")
    public ResponseEntity<ThongBao> retrieve(@PathVariable(value = "thongBaoId") long id) {
        return new ResponseEntity<>(this.thongBaoService.getThongBaoById(id), HttpStatus.OK);
    }

   
    @GetMapping("/secure/thong-bao/me")
    public ResponseEntity<?> getThongBaoByUser(Principal principal) {
        try {
            // Kiểm tra xem principal có hợp lệ không
            if (principal == null || principal.getName() == null) {
                return ResponseEntity.status(401).body(Map.of("error", "Chưa đăng nhập"));
            }

            // Lấy user từ username
            User user = userService.getUserByUsername(principal.getName());
            if (user == null) {
                return ResponseEntity.status(404).body(Map.of("error", "Không tìm thấy người dùng"));
            }

            // Lấy danh sách thông báo
            List<ThongBao> thongBaos = thongBaoService.getThongBaosByUserOrPublic(user);
            return ResponseEntity.ok(thongBaos);
        } catch (Exception ex) {
            return ResponseEntity.status(500).body(Map.of("error", "Lỗi hệ thống: " + ex.getMessage()));
        }
    }

    // Thêm endpoint để đánh dấu thông báo là đã đọc
    @PutMapping("/secure/thong-bao/{thongBaoId}/read")
    public ResponseEntity<?> markAsRead(@PathVariable(value = "thongBaoId") long id, Principal principal) {
        try {
            // Kiểm tra xem principal có hợp lệ không
            if (principal == null || principal.getName() == null) {
                return ResponseEntity.status(401).body(Map.of("error", "Chưa đăng nhập"));
            }

            // Lấy user từ username
            User user = userService.getUserByUsername(principal.getName());
            if (user == null) {
                return ResponseEntity.status(404).body(Map.of("error", "Không tìm thấy người dùng"));
            }

            // Kiểm tra thông báo có tồn tại không
            ThongBao thongBao = thongBaoService.getThongBaoById(id);
            if (thongBao == null) {
                return ResponseEntity.status(404).body(Map.of("error", "Không tìm thấy thông báo"));
            }

            // Kiểm tra quyền truy cập: chỉ cho phép user liên quan đánh dấu thông báo
            if (thongBao.getUser() != null && !thongBao.getUser().getId().equals(user.getId())) {
                return ResponseEntity.status(403).body(Map.of("error", "Không có quyền đánh dấu thông báo này"));
            }

            // Đánh dấu thông báo là đã đọc
            thongBaoService.markThongBaoAsRead(id);
            return ResponseEntity.ok(Map.of("message", "Đánh dấu thông báo đã đọc thành công"));
        } catch (Exception ex) {
            return ResponseEntity.status(500).body(Map.of("error", "Lỗi hệ thống: " + ex.getMessage()));
        }
    }
  
}
package com.desmin.controllers;

import com.desmin.pojo.HoatDongNgoaiKhoa;
import com.desmin.pojo.ThamGia;
import com.desmin.pojo.User;
import com.desmin.services.HoatDongNgoaiKhoaService;
import com.desmin.services.ThamGiaService;
import com.desmin.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class ApiThamGiaController {

   @Autowired
    private ThamGiaService thamGiaService;

    @Autowired
    private UserService userService;

    @Autowired
    private HoatDongNgoaiKhoaService hoatDongNgoaiKhoaService;


    @PostMapping("/secure/dangkys")
    public ResponseEntity<?> dangKyHoatDong(@RequestBody Map<String, Long> params) {
        try {
            // Lấy sinhVienId và hoatDongId từ params
            Long sinhVienId = params.get("sinhVienId");
            Long hoatDongId = params.get("hoatDongId");

            // Kiểm tra null
            if (sinhVienId == null) {
                return ResponseEntity.badRequest().body("Thiếu tham số sinhVienId");
            }
            if (hoatDongId == null) {
                return ResponseEntity.badRequest().body("Thiếu tham số hoatDongId");
            }

            // Lấy sinh viên và hoạt động từ service
            User sinhVien = userService.getUserById(sinhVienId);
            if (sinhVien == null) {
                return ResponseEntity.badRequest().body("Sinh viên không tồn tại với ID: " + sinhVienId);
            }

            HoatDongNgoaiKhoa hoatDong = hoatDongNgoaiKhoaService.getHoatDongNgoaiKhoaById(hoatDongId);
            if (hoatDong == null) {
                return ResponseEntity.badRequest().body("Hoạt động ngoại khóa không tồn tại với ID: " + hoatDongId);
            }

            // Gọi service để đăng ký
            thamGiaService.dangKyHoatDong(sinhVien, hoatDong);

            // Trả về phản hồi thành công
            Map<String, String> response = new HashMap<>();
            response.put("message", "Đăng ký hoạt động thành công");
            return ResponseEntity.ok(response);

        } catch (IllegalStateException e) {
            // Xử lý lỗi từ service (ví dụ: đã đăng ký)
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            // Xử lý lỗi khác
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi đăng ký hoạt động: " + e.getMessage());
        }
    }
    
    @GetMapping("/hoat-dong/{hoatDongId}")
    public ResponseEntity<?> getThamGiasByHoatDong(@PathVariable("hoatDongId") Long hoatDongId,
                                                  @RequestParam Map<String, String> params) {
        try {
            if (hoatDongId == null) {
                return ResponseEntity.badRequest().body("Thiếu tham số hoatDongId");
            }

            List<ThamGia> thamGias = thamGiaService.getThamGiasByHoatDongId(hoatDongId, params);
            return new ResponseEntity<>(thamGias, HttpStatus.OK);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi lấy danh sách sinh viên tham gia: " + e.getMessage());
        }
    }
    
    @PostMapping(path = "/diem-danh/csv", consumes = "multipart/form-data")
public ResponseEntity<?> diemDanhByCsv(@RequestParam("hoatDongId") Long hoatDongId,
                                      @RequestParam("file") MultipartFile file) {
    try {
        if (hoatDongId == null) {
            return ResponseEntity.badRequest().body("Thiếu tham số hoatDongId");
        }
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body("Thiếu file CSV");
        }

        thamGiaService.diemDanhByCsv(hoatDongId, file);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Điểm danh bằng CSV thành công");
        return ResponseEntity.ok(response);

    } catch (IllegalStateException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Lỗi khi điểm danh bằng CSV: " + e.getMessage());
    }
}
    
}

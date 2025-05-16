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
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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
    
    @PostMapping("/diem-danh-csv/{hoatDongId}")
    public ResponseEntity<?> diemDanhByCsv(@PathVariable("hoatDongId") Long hoatDongId,
                                          @RequestParam("file") MultipartFile file) {
        try {
            thamGiaService.diemDanhByCsv(hoatDongId, file);
            return ResponseEntity.ok(new HashMap<String, String>() {{
                put("message", "Điểm danh và cộng điểm thành công!");
            }});
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Lỗi khi điểm danh: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Lỗi khi xử lý file CSV: " + e.getMessage());
        }
    }

    
    @PostMapping("/diem-danh")
    public ResponseEntity<?> diemDanhHoatDong(@RequestParam Long sinhVienId, @RequestParam Long hoatDongId) {
        try {
            User sinhVien = userService.getUserById(sinhVienId);
            if (sinhVien == null) {
                return ResponseEntity.badRequest().body("Sinh viên không tồn tại");
            }
            HoatDongNgoaiKhoa hoatDong = hoatDongNgoaiKhoaService.getHoatDongNgoaiKhoaById(hoatDongId);
            if (hoatDong == null) {
                return ResponseEntity.badRequest().body("Hoạt động không tồn tại");
            }
            thamGiaService.diemDanhHoatDong(sinhVien, hoatDong);
            return ResponseEntity.ok("Điểm danh và cộng điểm thành công!");
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi khi điểm danh: " + e.getMessage());
        }
    }
    
    @GetMapping("/export-csv/{hoatDongId}")
    public ResponseEntity<byte[]> exportThamGiaToCsv(@PathVariable("hoatDongId") Long hoatDongId) {
        try {
            byte[] csvBytes = thamGiaService.exportThamGiaToCsv(hoatDongId);

            HttpHeaders headers = new HttpHeaders();
            String fileName = "tham_gia_hoat_dong_" + hoatDongId + ".csv";
            headers.setContentType(MediaType.parseMediaType("text/csv; charset=UTF-8"));
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"");
            headers.setContentLength(csvBytes.length);

            return new ResponseEntity<>(csvBytes, headers, HttpStatus.OK);

        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage().getBytes());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Lỗi khi xuất file CSV: " + e.getMessage()).getBytes());
        }
    }

     @GetMapping("/tham-gia/{sinhVienId}")
    public ResponseEntity<?> getThamGiaBySinhVien(
            @PathVariable("sinhVienId") Long sinhVienId,
            @RequestParam Map<String, String> params) {
        try {
            if (sinhVienId == null) {
                return ResponseEntity.badRequest().body("Thiếu tham số sinhVienId");
            }

            List<ThamGia> thamGias = thamGiaService.getThamGiaBySinhVienWithStates(sinhVienId, params);
            return new ResponseEntity<>(thamGias, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi lấy danh sách tham gia: " + e.getMessage());
        }
    }
    
}

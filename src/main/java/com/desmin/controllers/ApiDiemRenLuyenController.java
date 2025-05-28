/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.desmin.controllers;

import com.desmin.pojo.DiemRenLuyen;
import com.desmin.services.DiemRenLuyenService;
import com.desmin.services.DieuService;
import com.desmin.services.HocKyNamHocService;
import com.desmin.services.LopService;
import com.desmin.services.UserService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author ADMIN
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class ApiDiemRenLuyenController {

    @Autowired
    private DiemRenLuyenService diemRenLuyenService;
       @Autowired
    private LopService lopService;
          @Autowired
    private HocKyNamHocService hocKyNamHocService;
          
             @Autowired
    private DieuService dieuService;

    @Autowired
    private UserService userService;
    
        private static final Logger logger = LoggerFactory.getLogger(ApiDiemRenLuyenController.class);


    @GetMapping("/secure/diems/{userId}")
    public ResponseEntity<List<DiemRenLuyen>> getDiemRenLuyenBySinhVienId(
            @PathVariable("userId") long userId,
            @RequestParam(value = "namHoc", required = false) String namHoc,
            @RequestParam(value = "hocKy", required = false) String hocKy,
            @RequestParam(value = "page", defaultValue = "1") String page,
            @RequestParam(value = "size", defaultValue = "10") String size) {
        Map<String, String> params = new HashMap<>();
        if (namHoc != null) {
            params.put("namHoc", namHoc);
        }
        if (hocKy != null) {
            params.put("hocKy", hocKy);
        }
        params.put("page", page);
        params.put("size", size);
        try {
            List<DiemRenLuyen> drlList = diemRenLuyenService.getDiemRenLuyenBySinhVienId(userId, params);
            return new ResponseEntity<>(drlList, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
 @GetMapping("/secure/diems/")
public ResponseEntity<List<DiemRenLuyen>> getDiems(@RequestParam Map<String, String> params) {
    try {
        List<DiemRenLuyen> drlList = diemRenLuyenService.getDiemRenLuyens(params);
        return new ResponseEntity<>(drlList, HttpStatus.OK);
    } catch (IllegalArgumentException e) {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}


 
 @GetMapping("/secure/export/csv")
    public ResponseEntity<Resource> exportDiemRenLuyenToCsv(
            @RequestParam(name = "khoaId", required = false) Long khoaId,
            @RequestParam(name = "lopId", required = false) Long lopId,
            @RequestParam(name = "xepLoai", required = false) String xepLoai,
            @RequestParam(name = "hkNhId", required = false) Long hkNhId)
            {
        try {
            logger.info("Export CSV request: khoaId={}, lopId={}, xepLoai={}, hkNhId={}, page={}",
                    khoaId, lopId, xepLoai, hkNhId);

            // Xác thực xepLoai
            if (xepLoai != null && !isValidXepLoai(xepLoai)) {
                logger.warn("Invalid xepLoai: {}", xepLoai);
                return ResponseEntity.badRequest()
                        .body(new ByteArrayResource("Lỗi: Xếp loại không hợp lệ.".getBytes()));
            }

            byte[] csvData = diemRenLuyenService.exportDiemRenLuyenToCsv(khoaId, lopId, xepLoai, hkNhId);

            if (csvData == null || csvData.length == 0) {
                logger.warn("No data found for CSV export with given parameters");
                return ResponseEntity.badRequest()
                        .body(new ByteArrayResource("Lỗi: Không tìm thấy dữ liệu để xuất.".getBytes()));
            }

            ByteArrayResource resource = new ByteArrayResource(csvData);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=diem_ren_luyen.csv")
                    .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                    .contentLength(csvData.length)
                    .body(resource);
        } catch (Exception e) {
            logger.error("Error exporting CSV: {}", e.getMessage(), e);
            String errorMessage = "Lỗi server khi xuất CSV: " + e.getMessage();
            return ResponseEntity.badRequest()
                    .body(new ByteArrayResource(errorMessage.getBytes()));
        }
    }

    @GetMapping("/secure/export/pdf")
    public ResponseEntity<Resource> exportDiemRenLuyenToPdf(
            @RequestParam(name = "khoaId", required = false) Long khoaId,
            @RequestParam(name = "lopId", required = false) Long lopId,
            @RequestParam(name = "xepLoai", required = false) String xepLoai,
            @RequestParam(name = "hkNhId", required = false) Long hkNhId
          ) {
        try {
            logger.info("Export PDF request: khoaId={}, lopId={}, xepLoai={}, hkNhId={}",
                    khoaId, lopId, xepLoai, hkNhId);

            // Xác thực xepLoai
            if (xepLoai != null && !isValidXepLoai(xepLoai)) {
                logger.warn("Invalid xepLoai: {}", xepLoai);
                return ResponseEntity.badRequest()
                        .body(new ByteArrayResource("Lỗi: Xếp loại không hợp lệ.".getBytes()));
            }

            byte[] pdfData = diemRenLuyenService.exportDiemRenLuyenToPdf(khoaId, lopId, xepLoai, hkNhId);

            if (pdfData == null || pdfData.length == 0) {
                logger.warn("No data found for PDF export with given parameters");
                return ResponseEntity.badRequest()
                        .body(new ByteArrayResource("Lỗi: Không tìm thấy dữ liệu để xuất.".getBytes()));
            }

            ByteArrayResource resource = new ByteArrayResource(pdfData);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=diem_ren_luyen.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .contentLength(pdfData.length)
                    .body(resource);
        } catch (Exception e) {
            logger.error("Error exporting PDF: {}", e.getMessage(), e);
            String errorMessage = "Lỗi server khi xuất PDF: " + e.getMessage();
            return ResponseEntity.badRequest()
                    .body(new ByteArrayResource(errorMessage.getBytes()));
        }
    }

    private boolean isValidXepLoai(String xepLoai) {
        String[] validXepLoai = {"XUAT_SAC", "GIOI", "KHA", "TRUNG_BINH", "YEU", "KEM"};
        for (String valid : validXepLoai) {
            if (valid.equals(xepLoai)) {
                return true;
            }
        }
        return false;
    }
    
@GetMapping("/secure/thong-ke")
public ResponseEntity<?> thongKeDiemRenLuyen(
        @RequestParam(name = "khoaId", required = false) Long khoaId,
        @RequestParam(name = "lopId", required = false) Long lopId,
        @RequestParam(name = "xepLoai", required = false) String xepLoai,
        @RequestParam(name = "hkNhId", required = false) Long hkNhId) {
    try {
        logger.info("Statistics request: khoaId={}, lopId={}, xepLoai={}, hkNhId={}",
                khoaId, lopId, xepLoai, hkNhId);

        // Kiểm tra xepLoai hợp lệ
        if (xepLoai != null && !isValidXepLoai(xepLoai)) {
            logger.warn("Invalid xepLoai: {}", xepLoai);
            return ResponseEntity.badRequest()
                    .body("Lỗi: Xếp loại không hợp lệ.");
        }

        // Gọi service để lấy thống kê
        List<Map<String, Object>> thongKeData = diemRenLuyenService.thongKeDiemRenLuyen(khoaId, lopId, xepLoai, hkNhId);

        // Nếu không có dữ liệu, trả về thông điệp thân thiện
        if (thongKeData.isEmpty()) {
            logger.info("No data found for statistics with given parameters");
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Không có dữ liệu phù hợp");
            response.put("data", thongKeData);
            return ResponseEntity.ok(response);
        }

        return new ResponseEntity<>(thongKeData, HttpStatus.OK);
    } catch (Exception e) {
        logger.error("Error generating statistics: {}", e.getMessage(), e);
        return ResponseEntity.badRequest()
                .body("Lỗi server khi tạo thống kê: " + e.getMessage());
    }
}


    
}

package com.desmin.controllers;

import com.desmin.pojo.ThamGia;
import com.desmin.pojo.User;
import com.desmin.services.ThamGiaService;
import com.desmin.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class SinhVienController {

    @Autowired
    private UserService userService;

    @Autowired
    private ThamGiaService thamGiaService;

    @GetMapping("/sinh-vien")
    @PreAuthorize("hasAnyRole('CVCTSV', 'TRO_LY_SINH_VIEN')")
    public String listSinhVien(Model model, @RequestParam(name = "page", defaultValue = "1") String page) {
        try {
            int pageNumber;
            try {
                pageNumber = Integer.parseInt(page);
                if (pageNumber < 1) {
                    pageNumber = 1;
                }
            } catch (NumberFormatException e) {
                pageNumber = 1;
                model.addAttribute("error", "Tham số page không hợp lệ, sử dụng trang mặc định.");
            }

            Map<String, String> params = new HashMap<>();
            params.put("page", String.valueOf(pageNumber));
            List<User> sinhViens = userService.getAllSinhVien(params);

            model.addAttribute("sinhVienList", sinhViens);
            model.addAttribute("currentPage", pageNumber);

            return "sinh-vien-list";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi lấy danh sách sinh viên: " + e.getMessage());
            return "sinh-vien-list";
        }
    }

    @GetMapping("/sinh-vien/{sinhVienId}/thanh-tich")
    @PreAuthorize("hasAnyRole('CVCTSV', 'TRO_LY_SINH_VIEN')")
    public String xemThanhTich(Model model, @PathVariable("sinhVienId") Long sinhVienId) {
        try {
            // Lấy thông tin sinh viên
            User sinhVien = userService.getUserById(sinhVienId);
            if (sinhVien == null) {
                model.addAttribute("error", "Sinh viên không tồn tại với ID: " + sinhVienId);
                return "sinh-vien-thanh-tich";
            }

            // Lấy danh sách tham gia hoạt động ngoại khóa
            Map<String, String> params = new HashMap<>();
            List<ThamGia> thanhTichList = thamGiaService.getThamGiaBySinhVienWithStates(sinhVienId, params);

            // Tách danh sách thành hai: Điểm danh và Đăng ký
            List<ThamGia> diemDanhList = thanhTichList.stream()
                    .filter(thamGia -> "DiemDanh".equals(thamGia.getState().name()))
                    .collect(Collectors.toList());
            List<ThamGia> dangKyList = thanhTichList.stream()
                    .filter(thamGia -> "DangKy".equals(thamGia.getState().name()))
                    .collect(Collectors.toList());

            // Thêm dữ liệu vào model
            model.addAttribute("sinhVien", sinhVien);
            model.addAttribute("diemDanhList", diemDanhList);
            model.addAttribute("dangKyList", dangKyList);

            return "sinh-vien-thanh-tich";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi lấy danh sách thành tích: " + e.getMessage());
            return "sinh-vien-thanh-tich";
        }
    }
}
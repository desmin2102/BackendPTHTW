package com.desmin.controllers;

import com.desmin.pojo.DiemRenLuyenChiTiet;
import com.desmin.pojo.DiemRenLuyen;
import com.desmin.pojo.HoatDongNgoaiKhoa;
import com.desmin.pojo.ThamGia;
import com.desmin.pojo.User;
import com.desmin.services.DiemRenLuyenChiTietService;
import com.desmin.services.DiemRenLuyenService;
import com.desmin.services.HoatDongNgoaiKhoaService;
import com.desmin.services.ThamGiaService;
import com.desmin.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(SinhVienController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private ThamGiaService thamGiaService;

    @Autowired
    private DiemRenLuyenService diemRenLuyenService;

    @Autowired
    private DiemRenLuyenChiTietService drlctService;

    @Autowired
    private HoatDongNgoaiKhoaService hoatDongNgoaiKhoaService;

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

    @GetMapping("/sinh-vien/thanh-tich")
    @PreAuthorize("hasAnyRole('CVCTSV', 'TRO_LY_SINH_VIEN')")
    public String xemDanhSachDiemRenLuyen(
            Model model,
            @RequestParam(name = "sinhVienId") Long sinhVienId) {
        try {
            User sinhVien = userService.getUserById(sinhVienId);
            if (sinhVien == null) {
                model.addAttribute("error", "Sinh viên không tồn tại với ID: " + sinhVienId);
                return "sinh-vien-thanh-tich";
            }

            Map<String, String> params = new HashMap<>();
            List<DiemRenLuyen> diemRenLuyenList = diemRenLuyenService.getDiemRenLuyenBySinhVienId(sinhVienId, params);

            model.addAttribute("sinhVien", sinhVien);
            model.addAttribute("diemRenLuyenList", diemRenLuyenList);

            return "sinh-vien-thanh-tich";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi lấy danh sách điểm rèn luyện: " + e.getMessage());
            return "sinh-vien-thanh-tich";
        }
    }

    @GetMapping("/sinh-vien/{sinhVienId}/thanh-tich-chi-tiet")
    @PreAuthorize("hasAnyRole('CVCTSV', 'TRO_LY_SINH_VIEN')")
    public String xemThanhTichChiTiet(
            Model model,
            @PathVariable("sinhVienId") Long sinhVienId,
            @RequestParam(name = "diemRenLuyenId") Long diemRenLuyenId,
            @RequestParam(name = "hknhId") Long hknhId) {
        try {
            // Lấy thông tin sinh viên
            User sinhVien = userService.getUserById(sinhVienId);
            if (sinhVien == null) {
                model.addAttribute("error", "Sinh viên không tồn tại với ID: " + sinhVienId);
                return "sinh-vien-thanh-tich-chi-tiet";
            }

            // Lấy chi tiết điểm rèn luyện
            Map<String, String> params = new HashMap<>();
            params.put("noPaging", "true"); // Đảm bảo lấy tất cả bản ghi
            List<DiemRenLuyenChiTiet> diemChiTietList = drlctService.getDiemRenLuyenChiTietByDiemRenLuyenId(diemRenLuyenId, params);
            logger.info("DiemChiTietList for diemRenLuyenId {}: {}", diemRenLuyenId, diemChiTietList);

            if (diemChiTietList == null || diemChiTietList.isEmpty()) {
                model.addAttribute("error", "Không tìm thấy chi tiết điểm rèn luyện cho ID: " + diemRenLuyenId);
            }

            // Lấy danh sách HoatDongNgoaiKhoa theo hknhId
            Map<String, String> hoatDongParams = new HashMap<>();
            hoatDongParams.put("hknhId", String.valueOf(hknhId));
            hoatDongParams.put("noPaging", "true");
            List<HoatDongNgoaiKhoa> hoatDongs = hoatDongNgoaiKhoaService.getHoatDongNgoaiKhoas(hoatDongParams);
            List<Long> validHoatDongIds = hoatDongs.stream().map(HoatDongNgoaiKhoa::getId).collect(Collectors.toList());
            logger.info("ValidHoatDongIds for hknhId {}: {}", hknhId, validHoatDongIds);

            // Lấy danh sách tham gia hoạt động ngoại khóa
            Map<String, String> thamGiaParams = new HashMap<>();
            thamGiaParams.put("noPaging", "true");
            List<ThamGia> thanhTichList = thamGiaService.getThamGiaBySinhVienWithStates(sinhVienId, thamGiaParams);

            // Lọc ThamGia theo validHoatDongIds
            List<ThamGia> filteredThanhTichList = thanhTichList.stream()
                    .filter(thamGia -> validHoatDongIds.contains(thamGia.getHoatDongNgoaiKhoa().getId()))
                    .collect(Collectors.toList());
            logger.info("FilteredThanhTichList for sinhVienId {}: {}", sinhVienId, filteredThanhTichList);

            // Tách danh sách thành hai: Điểm danh và Đăng ký/Báo thiếu
            List<ThamGia> diemDanhList = filteredThanhTichList.stream()
                    .filter(thamGia -> "DiemDanh".equals(thamGia.getState().name()))
                    .collect(Collectors.toList());
            List<ThamGia> dangKyOrBaoThieuList = filteredThanhTichList.stream()
                    .filter(thamGia -> "DangKy".equals(thamGia.getState().name()) || "BaoThieu".equals(thamGia.getState().name()))
                    .collect(Collectors.toList());

            // Thêm dữ liệu vào model
            model.addAttribute("sinhVien", sinhVien);
            model.addAttribute("diemChiTietList", diemChiTietList);
            model.addAttribute("diemDanhList", diemDanhList);
            model.addAttribute("dangKyOrBaoThieuList", dangKyOrBaoThieuList);

            return "sinh-vien-thanh-tich-chi-tiet";
        } catch (Exception e) {
            logger.error("Lỗi khi lấy chi tiết thành tích: {}", e.getMessage(), e);
            model.addAttribute("error", "Lỗi khi lấy chi tiết thành tích: " + e.getMessage());
            return "sinh-vien-thanh-tich-chi-tiet";
        }
    }
}
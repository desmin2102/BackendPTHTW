package com.desmin.controllers;

import com.desmin.pojo.BaiViet;
import com.desmin.pojo.HoatDongNgoaiKhoa;
import com.desmin.pojo.ThamGia;
import com.desmin.pojo.User;
import com.desmin.services.BaiVietService;
import com.desmin.services.HoatDongNgoaiKhoaService;
import com.desmin.services.ThamGiaService;
import com.desmin.services.UserService;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class BaiVietController {

    @Autowired
    private BaiVietService baiVietService;

    @Autowired
    private ThamGiaService thamGiaService;

    @Autowired
    private UserService userService;

    @Autowired
    private HoatDongNgoaiKhoaService hoatDongNgoaiKhoaService;

    @Autowired
    private Cloudinary cloudinary;

    // Hiển thị danh sách bài viết công khai trên trang chủ
    @GetMapping({"/", "/home"})
    public String home(Model model, @RequestParam(required = false) Map<String, String> params) {
        try {
            List<BaiViet> baiViets = baiVietService.getAllBaiViet(params);
            model.addAttribute("baiViets", baiViets != null ? baiViets : new ArrayList<BaiViet>());
            // Tải danh sách hoạt động ngoại khóa
            List<HoatDongNgoaiKhoa> hoatDongs = hoatDongNgoaiKhoaService.getHoatDongNgoaiKhoas(new HashMap<>());
            model.addAttribute("hoatDongs", hoatDongs != null ? hoatDongs : new ArrayList<HoatDongNgoaiKhoa>());
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi tải dữ liệu: " + e.getMessage());
            model.addAttribute("baiViets", new ArrayList<BaiViet>());
            model.addAttribute("hoatDongs", new ArrayList<HoatDongNgoaiKhoa>());
        }
        return "index";
    }

    // Hiển thị danh sách bài viết cho quản lý (CVCTSV)
    @GetMapping("/baiviets")
    @PreAuthorize("hasRole('CVCTSV')")
    public String list(Model model, @RequestParam(required = false) Map<String, String> params) {
        
        try {
            
           
            List<BaiViet> baiViets = baiVietService.getAllBaiViet(params);
            model.addAttribute("baiViets", baiViets != null ? baiViets : new ArrayList<BaiViet>());
            // Tải danh sách hoạt động ngoại khóa
            List<HoatDongNgoaiKhoa> hoatDongs = hoatDongNgoaiKhoaService.getHoatDongNgoaiKhoas(new HashMap<>());
            model.addAttribute("hoatDongs", hoatDongs != null ? hoatDongs : new ArrayList<HoatDongNgoaiKhoa>());
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi tải dữ liệu: " + e.getMessage());
            model.addAttribute("baiViets", new ArrayList<BaiViet>());
            model.addAttribute("hoatDongs", new ArrayList<HoatDongNgoaiKhoa>());
        }
        return "baiviet-list";
    }

// Hiển thị form tạo bài viết mới
    @GetMapping("/baiviets/create")
    @PreAuthorize("hasRole('CVCTSV')")
    public String showCreateForm(Model model) {
        model.addAttribute("baiViet", new BaiViet());
        try {
            // Tạo params với noPaging=true để lấy toàn bộ danh sách
            Map<String, String> params = new HashMap<>();
            params.put("noPaging", "true");
            List<HoatDongNgoaiKhoa> hoatDongs = hoatDongNgoaiKhoaService.getHoatDongNgoaiKhoas(params);
            model.addAttribute("hoatDongs", hoatDongs != null ? hoatDongs : new ArrayList<HoatDongNgoaiKhoa>());
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi tải danh sách hoạt động: " + e.getMessage());
            model.addAttribute("hoatDongs", new ArrayList<HoatDongNgoaiKhoa>());
        }
        return "baiviet-form";
    }

    // Xử lý tạo bài viết mới
    @PostMapping("/baiviets/create")
    @PreAuthorize("hasRole('CVCTSV')")
    public String create(@ModelAttribute BaiViet baiViet,
                         @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                         RedirectAttributes redirectAttributes) {
        try {
            // Lấy thông tin người dùng hiện tại (CVCTSV)
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User currentUser = userService.getUserByUsername(username);

            if (currentUser == null || !currentUser.getRole().equals(User.Role.CVCTSV)) {
                throw new IllegalStateException("Bạn cần đăng nhập với vai trò CVCTSV để tạo bài viết.");
            }

            Map<String, String> params = new HashMap<>();
            // Kiểm tra các trường bắt buộc
            if (baiViet.getTitle() == null || baiViet.getTitle().trim().isEmpty() ||
                baiViet.getContent() == null || baiViet.getContent().trim().isEmpty()) {
                throw new IllegalArgumentException("Tiêu đề và nội dung không được để trống.");
            }

            // Kiểm tra hoatDongNgoaiKhoa (bắt buộc)
            if (baiViet.getHoatDongNgoaiKhoa() == null || baiViet.getHoatDongNgoaiKhoa().getId() == null) {
                throw new IllegalArgumentException("Vui lòng chọn một hoạt động ngoại khóa.");
            }

            params.put("title", baiViet.getTitle());
            params.put("content", baiViet.getContent());
            params.put("troLyId", String.valueOf(currentUser.getId()));
            params.put("hoatDongId", String.valueOf(baiViet.getHoatDongNgoaiKhoa().getId()));

            

            // Gọi service để lưu bài viết
            baiVietService.addBaiViet(params, imageFile);
            redirectAttributes.addFlashAttribute("message", "Tạo bài viết thành công!");
            return "redirect:/baiviets";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi tạo bài viết: " + e.getMessage());
            return "redirect:/baiviets/create";
        }
    }

    // Hiển thị chi tiết bài viết
    @GetMapping("/baiviets/{id}")
    public String getById(@PathVariable("id") long id, Model model, RedirectAttributes redirectAttributes) {
        BaiViet baiViet = baiVietService.getBaiVietById(id);
        if (baiViet != null) {
            model.addAttribute("baiViet", baiViet);
            // Kiểm tra hoatDongNgoaiKhoa để tránh lỗi null
            if (baiViet.getHoatDongNgoaiKhoa() != null) {
                HoatDongNgoaiKhoa hoatDong = baiViet.getHoatDongNgoaiKhoa();
                model.addAttribute("hoatDong", hoatDong);
            } else {
                model.addAttribute("hoatDong", null);
            }
            return "baiviet-detail";
        }
        redirectAttributes.addFlashAttribute("error", "Bài viết không tồn tại");
        return "redirect:/baiviets";
    }

    // Xóa bài viết
    @PostMapping("/baiviets/delete/{id}")
    @PreAuthorize("hasRole('CVCTSV')")
    public String deleteBaiViet(@PathVariable("id") long id, RedirectAttributes redirectAttributes) {
        try {
            baiVietService.deleteBaiViet(id);
            redirectAttributes.addFlashAttribute("message", "Xóa bài viết thành công");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi xóa bài viết: " + e.getMessage());
        }
        return "redirect:/baiviets";
    }
}
package com.desmin.controllers;

import com.desmin.pojo.BaiViet;
import com.desmin.pojo.Comment;
import com.desmin.pojo.Like;
import com.desmin.services.BaiVietService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Controller
public class BaiVietController {

    @Autowired
    private BaiVietService baiVietService;

    // Hiển thị danh sách bài viết công khai trên trang chủ
    @GetMapping({"/", "/home"})
    public String home(Model model, @RequestParam(required = false) Map<String, String> params) {
        List<BaiViet> baiViets = baiVietService.getAllBaiViet(params);
        model.addAttribute("baiViets", baiViets != null ? baiViets : new ArrayList<BaiViet>());
        return "index";
    }

    // Hiển thị danh sách bài viết cho quản lý (CVCTSV)
    @GetMapping("/baiviets")
    @PreAuthorize("hasRole('CVCTSV')")
    public String list(Model model, @RequestParam(required = false) Map<String, String> params) {
        List<BaiViet> baiViets = baiVietService.getAllBaiViet(params);
        model.addAttribute("baiViets", baiViets != null ? baiViets : new ArrayList<BaiViet>());
        return "baiviet-list";
    }

    // Hiển thị form tạo bài viết mới
    @GetMapping("/baiviets/create")
    @PreAuthorize("hasRole('CVCTSV')")
    public String showCreateForm(Model model) {
        model.addAttribute("baiViet", new BaiViet());
        return "baiviet-form";
    }

    // Xử lý tạo bài viết mới
    @PostMapping("/baiviets/create")
    @PreAuthorize("hasRole('CVCTSV')")
    public String create(@ModelAttribute BaiViet baiViet,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            RedirectAttributes redirectAttributes) {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("tieuDe", baiViet.getTitle());
            params.put("noiDung", baiViet.getContent());
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

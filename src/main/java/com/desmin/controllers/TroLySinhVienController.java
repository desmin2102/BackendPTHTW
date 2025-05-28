package com.desmin.controllers;

import com.desmin.pojo.Khoa;
import com.desmin.pojo.User;
import com.desmin.services.KhoaService;
import com.desmin.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class TroLySinhVienController {

    @Autowired
    private UserService userService;

    @Autowired
    private KhoaService khoaService;

    // Hiển thị form tạo trợ lý sinh viên
    @GetMapping("/tlsv/create")
    @PreAuthorize("hasRole('CVCTSV')")
    public String showCreateForm(Model model) {
        List<Khoa> khoaList = khoaService.getKhoas(new HashMap<>());
        model.addAttribute("khoaList", khoaList);
        model.addAttribute("troLy", new User());
        return "create-tlsv";
    }

    // Xử lý tạo trợ lý sinh viên
    @PostMapping("/tlsv/create")
    @PreAuthorize("hasRole('CVCTSV')")
    public String createTroLySinhVien(
            @ModelAttribute User troLy,
            @RequestParam(value = "avatar", required = false) MultipartFile avatar,
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            @RequestParam("email") String email,
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName,
            @RequestParam("khoaId") String khoaId, // Khớp với name="khoaId" trong HTML
            RedirectAttributes redirectAttributes) {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("username", username);
            params.put("password", password);
            params.put("email", email);
            params.put("ho", firstName); // Khớp với key "ho" mà service mong đợi
            params.put("ten", lastName); // Khớp với key "ten" mà service mong đợi
            params.put("khoaPhuTrach", khoaId); // Khớp với key "khoaPhuTrach" mà service mong đợi
            userService.addTroLySinhVien(params, avatar);
            redirectAttributes.addFlashAttribute("message", "Tạo trợ lý sinh viên thành công!");
            return "redirect:/";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/tlsv/create";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi không xác định khi tạo trợ lý sinh viên: " + e.getMessage());
            return "redirect:/tlsv/create";
        }
    }
}
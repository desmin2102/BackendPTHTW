package com.desmin.controllers;

import com.desmin.pojo.HocKyNamHoc;
import com.desmin.services.HocKyNamHocService;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller xử lý quản lý học kỳ/năm học cho CVCTSV.
 *
 * @author admin
 */
@Controller
public class HocKyNamHocController {

    @Autowired
    private HocKyNamHocService hocKyNamHocService;

    @GetMapping("/hoc-ky-nam-hoc")
    @PreAuthorize("hasRole('CVCTSV')")
    public String listHocKyNamHoc(Model model) {
        try {
            List<HocKyNamHoc> hocKyNamHocs = hocKyNamHocService.getHocKyNamHocs(new HashMap<>());
            model.addAttribute("hocKyNamHocs", hocKyNamHocs != null ? hocKyNamHocs : Collections.emptyList());
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi lấy danh sách học kỳ/năm học: " + e.getMessage());
            model.addAttribute("hocKyNamHocs", Collections.emptyList());
        }
        return "hoc-ky-nam-hoc";
    }

    @GetMapping("/hoc-ky-nam-hoc/create")
    @PreAuthorize("hasRole('CVCTSV')")
    public String createHocKyNamHocForm(Model model) {
        model.addAttribute("hocKyNamHoc", new HocKyNamHoc());
        // Truyền danh sách giá trị enum HocKy
        model.addAttribute("hocKyOptions", HocKyNamHoc.HocKy.values());
        return "create-hoc-ky-nam-hoc";
    }

    @PostMapping("/hoc-ky-nam-hoc/create")
    @PreAuthorize("hasRole('CVCTSV')")
    public String createHocKyNamHoc(@ModelAttribute HocKyNamHoc hocKyNamHoc, RedirectAttributes redirectAttributes) {
        try {
            hocKyNamHocService.createHocKyNamHoc(hocKyNamHoc);
            redirectAttributes.addFlashAttribute("message", "Tạo học kỳ/năm học thành công!");
            return "redirect:/hoc-ky-nam-hoc";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi tạo học kỳ/năm học: " + e.getMessage());
            return "redirect:/hoc-ky-nam-hoc/create";
        }
    }
}
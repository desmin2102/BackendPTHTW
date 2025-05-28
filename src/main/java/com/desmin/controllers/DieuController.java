/*
 * Click nbfs://nbproject/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbproject://nbproject/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.desmin.controllers;

import com.desmin.pojo.Dieu;
import com.desmin.services.DieuService;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller xử lý quản lý tiêu chí (Điều) cho CVCTSV.
 *
 * @author admin
 */
@Controller
public class DieuController {

    @Autowired
    private DieuService dieuService;

    @GetMapping("/dieu")
    @PreAuthorize("hasRole('CVCTSV')")
    public String listDieu(Model model) {
        try {
            List<Dieu> dieus = dieuService.getDieus(new HashMap<>());
            model.addAttribute("dieus", dieus != null ? dieus : Collections.emptyList());
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi lấy danh sách tiêu chí: " + e.getMessage());
            model.addAttribute("dieus", Collections.emptyList());
        }
        return "dieu";
    }
}
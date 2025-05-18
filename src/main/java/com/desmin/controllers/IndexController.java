package com.desmin.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    @GetMapping("/cvctsv")
    @PreAuthorize("hasRole('CVCTSV')")
    public String cvctsv(Model model) {
        return "cvctsv"; // Template: cvctsv.html (trang quản lý cho CVCTSV)
    }
}
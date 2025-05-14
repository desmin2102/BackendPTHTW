/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.desmin.controllers;

import com.desmin.pojo.DiemRenLuyen;
import com.desmin.services.DiemRenLuyenService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

 @GetMapping("/diems/{userId}")
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
}

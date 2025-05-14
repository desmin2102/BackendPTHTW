/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.desmin.controllers;

import com.desmin.pojo.DiemRenLuyenChiTiet;
import com.desmin.services.DiemRenLuyenChiTietService;
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
public class ApiDiemRenLuyenChiTietController {
    @Autowired
    private DiemRenLuyenChiTietService drlctService;
    
@GetMapping("/drlcts/{drlId}")
    public ResponseEntity<List<DiemRenLuyenChiTiet>> getDiemRenLuyenChiTietByDiemRenLuyenId(
            @PathVariable("drlId") long drlId,
            @RequestParam Map<String, String> params) {

        List<DiemRenLuyenChiTiet> diemRenLuyenChiTiets = drlctService.getDiemRenLuyenChiTietByDiemRenLuyenId(drlId, params);

        if (diemRenLuyenChiTiets.isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(diemRenLuyenChiTiets, HttpStatus.OK);
    }
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.desmin.controllers;

import com.desmin.pojo.HoatDongNgoaiKhoa;
import com.desmin.services.HoatDongNgoaiKhoaService;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
public class ApiHoatDongNgoaiKhoaController {

    @Autowired
    private HoatDongNgoaiKhoaService hdnkService;

    @GetMapping("/hdnks")
    public ResponseEntity<List<HoatDongNgoaiKhoa>> list(@RequestParam Map<String, String> params) {
        return new ResponseEntity<>(this.hdnkService.getHoatDongNgoaiKhoas(params), HttpStatus.OK);
    }

    @PostMapping("secure/hdnks")
    public ResponseEntity<HoatDongNgoaiKhoa> create(@RequestBody HoatDongNgoaiKhoa hoatDong) {
        this.hdnkService.addHoatDongNgoaiKhoa(hoatDong);
        return new ResponseEntity<>(hoatDong, HttpStatus.CREATED);

    }


}

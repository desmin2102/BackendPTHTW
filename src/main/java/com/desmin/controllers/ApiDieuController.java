/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.desmin.controllers;

import com.desmin.pojo.Dieu;
import com.desmin.services.DieuService;
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
public class ApiDieuController {

    @Autowired
    private DieuService dieuService;

    @GetMapping("/dieus")
    public ResponseEntity<List<Dieu>> list(@RequestParam Map<String, String> params) {
        return new ResponseEntity<>(this.dieuService.getDieus(params), HttpStatus.OK);
    }

    @GetMapping("/dieus/{dieuId}")
    public ResponseEntity<Dieu> retrieve(@PathVariable(value = "dieuId") int id) {
        return new ResponseEntity<>(this.dieuService.getDieuById(id), HttpStatus.OK);
    }
}

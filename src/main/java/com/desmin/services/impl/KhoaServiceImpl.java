/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.desmin.services.impl;

import com.desmin.pojo.Khoa;
import com.desmin.repositories.KhoaRepository;
import com.desmin.services.KhoaService;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author ADMIN
 */
@Service
@Transactional
public class KhoaServiceImpl implements KhoaService{

    @Autowired
    private KhoaRepository khoaRepo;
    
    @Override
    public List<Khoa> getKhoas(Map<String, String> params) {
        return this.khoaRepo.getKhoas(params);
    }

    
    @Override
    public Khoa getKhoaById(int id) {
        return this.khoaRepo.getKhoaById(id);
    }
    
}

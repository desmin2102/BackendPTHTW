/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.desmin.services.impl;

import com.desmin.pojo.Dieu;
import com.desmin.pojo.HocKyNamHoc;
import com.desmin.repositories.DieuRepository;
import com.desmin.services.DieuService;
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
public class DieuServiceImpl implements DieuService{
     @Autowired
    private DieuRepository dieuRepo;

    @Override
    public List<Dieu> getDieus(Map<String, String> params) {
                return this.dieuRepo.getDieus(params);

    }


    @Override
    public Dieu getDieuById(long id) {
                return this.dieuRepo.getDieuById(id);
    }
    
    
}

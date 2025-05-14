/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.desmin.services.impl;

import com.desmin.pojo.HocKyNamHoc;
import com.desmin.repositories.HocKyNamHocRepository;
import com.desmin.repositories.KhoaRepository;
import com.desmin.services.HocKyNamHocService;
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
public class HocKyNamHocServiceImpl implements HocKyNamHocService{
     @Autowired
    private HocKyNamHocRepository hknhRepo;

    @Override
    public List<HocKyNamHoc> getHocKyNamHocs(Map<String, String> params) {
                return this.hknhRepo.getHocKyNamHocs(params);

    }

    @Override
    public HocKyNamHoc getHocKyNamHocById(long id) {
                return this.hknhRepo.getHocKyNamHocById(id);

    }
    
    
}

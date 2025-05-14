/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.desmin.services.impl;

import com.desmin.pojo.Lop;
import com.desmin.services.LopService;
import com.desmin.repositories.LopRepository;
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
public class LopServiceImpl implements LopService {

    @Autowired
    private LopRepository lopRepo;

    @Override
    public List<Lop> getLops(Map<String, String> params) {
        return this.lopRepo.getLops(params);
    }

    @Transactional
    @Override
    public Lop getLopById(int id) {
        return this.lopRepo.getLopById(id);
    }

}

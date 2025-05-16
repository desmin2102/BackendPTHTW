/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.desmin.services.impl;

import com.desmin.pojo.HoatDongNgoaiKhoa;
import com.desmin.pojo.User;
import com.desmin.repositories.HoatDongNgoaiKhoaRepository;
import com.desmin.repositories.ThamGiaRepository;
import com.desmin.services.HoatDongNgoaiKhoaService;
import java.time.LocalDate;
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
public class HoatDongNgoaiKhoaServiceImpl implements HoatDongNgoaiKhoaService{

 @Autowired
    private HoatDongNgoaiKhoaRepository hdnkRepo;
 @Autowired
    private ThamGiaRepository thamGiaRepo;

    @Transactional
    @Override
    public List<HoatDongNgoaiKhoa> getHoatDongNgoaiKhoas(Map<String, String> params) {
        return this.hdnkRepo.getHoatDongNgoaiKhoas(params);
    }

    @Transactional
    @Override
    public HoatDongNgoaiKhoa getHoatDongNgoaiKhoaById(long id) {
        return this.hdnkRepo.getHoatDongNgoaiKhoaById(id);
    }

    @Transactional
    @Override
    public List<HoatDongNgoaiKhoa> findByHanDangKyBefore(LocalDate date) {
        return this.hdnkRepo.findByHanDangKyBefore(date);
    }

    @Transactional
    @Override
    public List<HoatDongNgoaiKhoa> findActiveAndNotExpired(LocalDate date) {
        return this.hdnkRepo.findActiveAndNotExpired(date);
    }

    @Transactional
    @Override
    public void update(HoatDongNgoaiKhoa hoatDong) {
        this.hdnkRepo.update(hoatDong);
    }



    @Transactional
    @Override
    public HoatDongNgoaiKhoa addHoatDongNgoaiKhoa(HoatDongNgoaiKhoa h) {
     return this.hdnkRepo.addHoatDongNgoaiKhoa(h);

    }
    
    
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.desmin.services.impl;

import com.desmin.pojo.DiemRenLuyenChiTiet;
import com.desmin.repositories.DiemRenLuyenChiTietRepository;
import com.desmin.services.DiemRenLuyenChiTietService;
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
public class DiemRenLuyenChiTietServiceImpl implements DiemRenLuyenChiTietService{
    
   
    @Autowired
    private DiemRenLuyenChiTietRepository diemChiTietRepository;

    @Override
    public List<DiemRenLuyenChiTiet> getDiemRenLuyenChiTietByDiemRenLuyenId(Long diemRenLuyenId, Map<String, String> params) {
        return diemChiTietRepository.getDiemRenLuyenChiTietByDiemRenLuyenId(diemRenLuyenId, params);
    }

    @Override
    public void saveDiemRenLuyenChiTiet(DiemRenLuyenChiTiet chiTiet) {
        diemChiTietRepository.saveDiemRenLuyenChiTiet(chiTiet);
    }

}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.desmin.repositories;

import com.desmin.pojo.DiemRenLuyenChiTiet;
import java.util.List;
import java.util.Map;

/**
 *
 * @author ADMIN
 */
public interface DiemRenLuyenChiTietRepository {
    
     List<DiemRenLuyenChiTiet> getDiemRenLuyenChiTiets(Map<String, String> params);

    List<DiemRenLuyenChiTiet> getDiemRenLuyenChiTietByDiemRenLuyenId(long drlId, Map<String, String> params);
    
    void saveDiemRenLuyenChiTiet(DiemRenLuyenChiTiet chiTiet);
    
}

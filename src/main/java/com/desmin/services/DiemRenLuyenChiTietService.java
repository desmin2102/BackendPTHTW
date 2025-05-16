/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.desmin.services;

import com.desmin.pojo.DiemRenLuyenChiTiet;
import java.util.List;
import java.util.Map;

/**
 *
 * @author ADMIN
 */
public interface DiemRenLuyenChiTietService {
       // Lấy danh sách chi tiết điểm rèn luyện theo diemRenLuyenId
    List<DiemRenLuyenChiTiet> getDiemRenLuyenChiTietByDiemRenLuyenId(Long diemRenLuyenId, Map<String, String> params);

    // Lưu hoặc cập nhật chi tiết điểm rèn luyện
    void saveDiemRenLuyenChiTiet(DiemRenLuyenChiTiet chiTiet);
}

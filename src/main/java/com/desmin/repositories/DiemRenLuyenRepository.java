/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.desmin.repositories;

import com.desmin.pojo.DiemRenLuyen;
import com.desmin.pojo.HocKyNamHoc;
import com.desmin.pojo.User;
import java.util.List;
import java.util.Map;

/**
 *
 * @author ADMIN
 */
public interface DiemRenLuyenRepository {

    List<DiemRenLuyen> getDiemRenLuyens(Map<String, String> params);

    List<DiemRenLuyen> getDiemRenLuyenBySinhVienId(long userId, Map<String, String> params);

    void saveDiemRenLuyen(DiemRenLuyen diemRenLuyen);

    DiemRenLuyen findBySinhVienAndHkNh(User sinhVien, HocKyNamHoc hkNh);
}

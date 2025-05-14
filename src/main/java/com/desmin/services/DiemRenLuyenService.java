package com.desmin.services;

import com.desmin.pojo.DiemRenLuyen;
import com.desmin.pojo.HoatDongNgoaiKhoa;
import com.desmin.pojo.User;
import java.util.List;
import java.util.Map;

public interface DiemRenLuyenService {

    List<DiemRenLuyen> getDiemRenLuyens(Map<String, String> params);

    List<DiemRenLuyen> getDiemRenLuyenBySinhVienId(long userId, Map<String, String> params);
    
    void congDiemRenLuyen(User sinhVien, HoatDongNgoaiKhoa hoatDong);
}
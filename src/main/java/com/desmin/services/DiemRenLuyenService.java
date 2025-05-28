package com.desmin.services;

import com.desmin.pojo.DiemRenLuyen;
import com.desmin.pojo.HoatDongNgoaiKhoa;
import com.desmin.pojo.User;
import java.util.List;
import java.util.Map;

public interface DiemRenLuyenService {
    // Lấy danh sách điểm rèn luyện theo tham số
    List<DiemRenLuyen> getDiemRenLuyens(Map<String, String> params);

    // Lấy danh sách điểm rèn luyện theo sinh viên
    List<DiemRenLuyen> getDiemRenLuyenBySinhVienId(long userId, Map<String, String> params);

    // Cộng điểm rèn luyện cho sinh viên
    void congDiemRenLuyen(User sinhVien, HoatDongNgoaiKhoa hoatDong);
        List<DiemRenLuyen> getDiemRenLuyenTongHop(Long khoaId, Long lopId, String xepLoai, Long hkNhId, int page, int size);
  byte[] exportDiemRenLuyenToCsv(Long khoaId, Long lopId, String xepLoai, Long hkNhId);
    byte[] exportDiemRenLuyenToPdf(Long khoaId, Long lopId, String xepLoai, Long hkNhId);
List<Map<String, Object>> thongKeDiemRenLuyen(Long khoaId, Long lopId, String xepLoai, Long hkNhId);
}
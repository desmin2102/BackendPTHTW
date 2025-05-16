package com.desmin.repositories;

import com.desmin.pojo.DiemRenLuyen;
import com.desmin.pojo.HocKyNamHoc;
import com.desmin.pojo.User;
import java.util.List;
import java.util.Map;

/**
 * Giao diện repository cho DiemRenLuyen.
 */
public interface DiemRenLuyenRepository {

    // Lấy danh sách điểm rèn luyện theo tham số
    List<DiemRenLuyen> getDiemRenLuyens(Map<String, String> params);

    // Lấy danh sách điểm rèn luyện theo sinh viên
    List<DiemRenLuyen> getDiemRenLuyenBySinhVienId(long userId, Map<String, String> params);

    // Tìm điểm rèn luyện theo sinh viên và học kỳ năm học
    DiemRenLuyen findBySinhVienAndHkNh(User sinhVien, HocKyNamHoc hkNh);

    // Lưu hoặc cập nhật điểm rèn luyện
    void saveDiemRenLuyen(DiemRenLuyen diemRenLuyen);
        DiemRenLuyen createDiemRenLuyen(User sinhVien, HocKyNamHoc hkNh);

}
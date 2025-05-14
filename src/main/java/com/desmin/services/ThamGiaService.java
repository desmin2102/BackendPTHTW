package com.desmin.services;

import com.desmin.pojo.HoatDongNgoaiKhoa;
import com.desmin.pojo.ThamGia;
import com.desmin.pojo.User;
import java.util.List;
import java.util.Map;
import org.springframework.web.multipart.MultipartFile;

/**
 * Giao diện service cho ThamGia.
 */
public interface ThamGiaService {

    // Lấy danh sách tham gia với các tham số lọc
    List<ThamGia> getThamGias(Map<String, String> params);

    // Lấy danh sách tham gia của một sinh viên
    List<ThamGia> getThamGiasBySinhVienId(long sinhVienId, Map<String, String> params);

    // Lấy danh sách tham gia theo hoạt động ngoại khóa
    List<ThamGia> getThamGiasByHoatDongId(long hoatDongId, Map<String, String> params);

    // Lấy tham gia theo sinh viên và hoạt động
    ThamGia findBySinhVienAndHoatDong(User sinhVien, HoatDongNgoaiKhoa hoatDong);

    // Lưu hoặc cập nhật tham gia
    void saveThamGia(ThamGia thamGia);

    // Đăng ký hoạt động ngoại khóa
    void dangKyHoatDong(User sinhVien, HoatDongNgoaiKhoa hoatDong);

void diemDanhByCsv(Long hoatDongId, MultipartFile file); // Thêm phương thức điểm danh bằng CSV    
}
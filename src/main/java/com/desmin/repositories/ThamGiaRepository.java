package com.desmin.repositories;

import com.desmin.pojo.HoatDongNgoaiKhoa;
import com.desmin.pojo.ThamGia;
import com.desmin.pojo.User;
import java.util.List;
import java.util.Map;
import org.springframework.web.multipart.MultipartFile;

/**
 * Giao diện repository cho ThamGia.
 */
public interface ThamGiaRepository {

    // Lấy danh sách tham gia theo tham số
    List<ThamGia> getThamGias(Map<String, String> params);
        ThamGia getThamGiaById(long id);


    // Lấy danh sách tham gia theo sinh viên
    List<ThamGia> getThamGiasBySinhVienId(long sinhVienId, Map<String, String> params);
    

    // Lấy danh sách tham gia theo hoạt động ngoại khóa
    List<ThamGia> getThamGiasByHoatDongId(long hoatDongId, Map<String, String> params);

    // Lấy tham gia theo sinh viên và hoạt động
    ThamGia findBySinhVienAndHoatDong(User sinhVien, HoatDongNgoaiKhoa hoatDong);

    // Lưu hoặc cập nhật tham gia
    void saveThamGia(ThamGia thamGia);

    // Đăng ký hoạt động ngoại khóa
    void dangKyHoatDong(User sinhVien, HoatDongNgoaiKhoa hoatDong);

    // Điểm danh hoạt động ngoại khóa
    void diemDanhHoatDong(User sinhVien, HoatDongNgoaiKhoa hoatDong);

    void diemDanhByCsv(HoatDongNgoaiKhoa hoatDong, MultipartFile file); // Thêm phương thức mới

        
         List<ThamGia> getThamGiaBySinhVienWithStates(long sinhVienId, List<ThamGia.TrangThai> states, Map<String, String> params);


}

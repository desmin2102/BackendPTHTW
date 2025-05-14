package com.desmin.services.impl;

import com.desmin.pojo.HoatDongNgoaiKhoa;
import com.desmin.pojo.ThamGia;
import com.desmin.pojo.User;
import com.desmin.repositories.HoatDongNgoaiKhoaRepository;
import com.desmin.repositories.ThamGiaRepository;
import com.desmin.services.ThamGiaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
public class ThamGiaServiceImpl implements ThamGiaService {

    @Autowired
    private ThamGiaRepository thamGiaRepository;
     @Autowired
    private HoatDongNgoaiKhoaRepository hoatDongNgoaiKhoaRepository;



    // Phương thức này lấy danh sách tham gia với các tham số lọc
    @Override
    public List<ThamGia> getThamGias(Map<String, String> params) {
        return thamGiaRepository.getThamGias(params);
    }

    // Phương thức này lấy danh sách tham gia của một sinh viên
    @Override
    public List<ThamGia> getThamGiasBySinhVienId(long sinhVienId, Map<String, String> params) {
        return thamGiaRepository.getThamGiasBySinhVienId(sinhVienId, params);
    }

    // Phương thức này lấy danh sách tham gia theo hoạt động ngoại khóa
    @Override
    public List<ThamGia> getThamGiasByHoatDongId(long hoatDongId, Map<String, String> params) {
        return thamGiaRepository.getThamGiasByHoatDongId(hoatDongId, params);
    }

    // Phương thức này tìm tham gia theo sinh viên và hoạt động
    @Override
    public ThamGia findBySinhVienAndHoatDong(User sinhVien, HoatDongNgoaiKhoa hoatDong) {
        return thamGiaRepository.findBySinhVienAndHoatDong(sinhVien, hoatDong);
    }

    // Phương thức này lưu hoặc cập nhật tham gia
    @Override
    public void saveThamGia(ThamGia thamGia) {
        thamGiaRepository.saveThamGia(thamGia);
    }

    // Phương thức này cho phép sinh viên đăng ký hoạt động ngoại khóa
    @Override
    public void dangKyHoatDong(User sinhVien, HoatDongNgoaiKhoa hoatDong) {
        thamGiaRepository.dangKyHoatDong(sinhVien, hoatDong);
    }

    @Override
    public void diemDanhByCsv(Long hoatDongId, MultipartFile file) {
        // Kiểm tra hoatDongId
        HoatDongNgoaiKhoa hoatDong = hoatDongNgoaiKhoaRepository.getHoatDongNgoaiKhoaById(hoatDongId);
        if (hoatDong == null) {
            throw new IllegalArgumentException("Hoạt động ngoại khóa không tồn tại với ID: " + hoatDongId);
        }

        thamGiaRepository.diemDanhByCsv(hoatDong, file);
    }
    
}
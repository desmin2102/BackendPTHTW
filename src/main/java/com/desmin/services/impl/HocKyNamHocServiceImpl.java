/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.desmin.services.impl;

import com.desmin.pojo.HocKyNamHoc;
import com.desmin.pojo.User;
import com.desmin.repositories.DiemRenLuyenChiTietRepository;
import com.desmin.repositories.DiemRenLuyenRepository;
import com.desmin.repositories.HocKyNamHocRepository;
import com.desmin.repositories.UserRepository;
import com.desmin.services.HocKyNamHocService;
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
public class HocKyNamHocServiceImpl implements HocKyNamHocService{
     @Autowired
    private HocKyNamHocRepository hknhRepo;
       @Autowired
    private DiemRenLuyenRepository diemRenLuyenRepository;

    @Autowired
    private DiemRenLuyenChiTietRepository diemChiTietRepository;
      @Autowired
    private UserRepository userRepo;

    @Override
    public List<HocKyNamHoc> getHocKyNamHocs(Map<String, String> params) {
                return this.hknhRepo.getHocKyNamHocs(params);

    }

    @Override
    public HocKyNamHoc getHocKyNamHocById(long id) {
                return this.hknhRepo.getHocKyNamHocById(id);

    }
    
@Override
    public void createHocKyNamHoc(HocKyNamHoc hocKyNamHoc) {
        // Kiểm tra dữ liệu đầu vào
        if (hocKyNamHoc.getHocKy() == null || hocKyNamHoc.getNamHoc() == null ||
            hocKyNamHoc.getStartDate() == null || hocKyNamHoc.getEndDate() == null) {
            throw new IllegalArgumentException("Thông tin học kỳ năm học không đầy đủ");
        }
        if (hocKyNamHoc.getStartDate().isAfter(hocKyNamHoc.getEndDate())) {
            throw new IllegalArgumentException("startDate phải trước endDate");
        }

        // Kiểm tra trùng lấn thời gian
        List<HocKyNamHoc> overlapping = hknhRepo.findOverlappingHocKyNamHoc(
            hocKyNamHoc.getStartDate(), hocKyNamHoc.getEndDate()
        );
        if (!overlapping.isEmpty()) {
            throw new IllegalArgumentException("Học kỳ trùng lấn thời gian với: " + overlapping.get(0).getNamHoc() + ", học kỳ " + overlapping.get(0).getHocKy());
        }

        // Kiểm tra ràng buộc hocKy và namHoc
        if (hknhRepo.existsByHocKyAndNamHoc(hocKyNamHoc.getHocKy(), hocKyNamHoc.getNamHoc())) {
            throw new IllegalArgumentException("Học kỳ " + hocKyNamHoc.getHocKy() + " đã tồn tại cho năm học " + hocKyNamHoc.getNamHoc());
        }

        // Đặt active mặc định
        hocKyNamHoc.setActive(true);

        // Lưu HocKyNamHoc
        hknhRepo.createHocKyNamHoc(hocKyNamHoc);

        // Tạo DiemRenLuyen và DiemRenLuyenChiTiet cho tất cả sinh viên
        List<User> sinhViens = userRepo.findAllSinhVien();
        for (User sinhVien : sinhViens) {
            diemRenLuyenRepository.createDiemRenLuyen(sinhVien, hocKyNamHoc);
            diemChiTietRepository.createDiemRenLuyenChiTietForAllDieu(
                diemRenLuyenRepository.findBySinhVienAndHkNh(sinhVien, hocKyNamHoc)
            );
        }
    }
    
}

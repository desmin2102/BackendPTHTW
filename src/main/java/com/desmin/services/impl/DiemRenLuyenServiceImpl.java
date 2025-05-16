package com.desmin.services.impl;

import com.desmin.pojo.DiemRenLuyen;
import com.desmin.pojo.DiemRenLuyenChiTiet;
import com.desmin.pojo.Dieu;
import com.desmin.pojo.HoatDongNgoaiKhoa;
import com.desmin.pojo.HocKyNamHoc;
import com.desmin.pojo.User;
import com.desmin.repositories.DiemRenLuyenChiTietRepository;
import com.desmin.repositories.DiemRenLuyenRepository;
import com.desmin.repositories.DieuRepository;
import com.desmin.services.DiemRenLuyenService;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DiemRenLuyenServiceImpl implements DiemRenLuyenService {

    @Autowired
    private DiemRenLuyenRepository diemRenLuyenRepository;

    @Autowired
    private DiemRenLuyenChiTietRepository diemChiTietRepository;
      @Autowired
    private DieuRepository dieuRepository;


    @Override
    public List<DiemRenLuyen> getDiemRenLuyens(Map<String, String> params) {
        return diemRenLuyenRepository.getDiemRenLuyens(params);
    }

    @Override
    public List<DiemRenLuyen> getDiemRenLuyenBySinhVienId(long userId, Map<String, String> params) {
        return diemRenLuyenRepository.getDiemRenLuyenBySinhVienId(userId, params);
    }

    @Override
    public void congDiemRenLuyen(User sinhVien, HoatDongNgoaiKhoa hoatDong) {
        HocKyNamHoc hkNh = hoatDong.getHkNh();
        Integer diem = hoatDong.getDiemRenLuyen();
        Dieu dieu = hoatDong.getDieu();

        // Kiểm tra dieu hợp lệ
        if (dieu == null) {
            throw new IllegalArgumentException("Hoạt động không có Điều liên kết");
        }

        // Tạo hoặc lấy DiemRenLuyen
        DiemRenLuyen diemRenLuyen = diemRenLuyenRepository.createDiemRenLuyen(sinhVien, hkNh);

        // Tạo DiemRenLuyenChiTiet cho tất cả Dieu nếu chưa có
        diemChiTietRepository.createDiemRenLuyenChiTietForAllDieu(diemRenLuyen);

        // Cộng điểm vào DiemRenLuyenChiTiet
        List<DiemRenLuyenChiTiet> chiTiets = diemChiTietRepository.getDiemRenLuyenChiTietByDiemRenLuyenId(
            diemRenLuyen.getId(), new HashMap<>()
        );
        DiemRenLuyenChiTiet chiTiet = chiTiets.stream()
            .filter(ct -> ct.getDieu().getId().equals(dieu.getId()))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Không tìm thấy DiemRenLuyenChiTiet cho Dieu: " + dieu.getId()));

        // Tính điểm còn lại
        int tongDiemHienTai = chiTiet.getDiem();
        int diemConLai = dieu.getDiemToiDa() - tongDiemHienTai;
        if (diemConLai <= 0) {
            return;
        }

        int diemThucTe = Math.min(diem, diemConLai);

        // Cập nhật DiemRenLuyenChiTiet
        chiTiet.setDiem(tongDiemHienTai + diemThucTe);
        diemChiTietRepository.saveDiemRenLuyenChiTiet(chiTiet);

        // Cập nhật DiemRenLuyen
        diemRenLuyen.setDiemTong(diemRenLuyen.getDiemTong() + diemThucTe);
        diemRenLuyen.setUpdatedDate(LocalDateTime.now());
        diemRenLuyenRepository.saveDiemRenLuyen(diemRenLuyen);
    }
}

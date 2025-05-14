package com.desmin.services.impl;

import com.desmin.pojo.DiemRenLuyen;
import com.desmin.pojo.DiemRenLuyenChiTiet;
import com.desmin.pojo.Dieu;
import com.desmin.pojo.HoatDongNgoaiKhoa;
import com.desmin.pojo.HocKyNamHoc;
import com.desmin.pojo.User;
import com.desmin.repositories.DiemRenLuyenChiTietRepository;
import com.desmin.repositories.DiemRenLuyenRepository;
import com.desmin.services.DiemRenLuyenService;
import java.time.LocalDateTime;
import java.util.HashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DiemRenLuyenServiceImpl implements DiemRenLuyenService {

    @Autowired
    private DiemRenLuyenRepository diemRenLuyenRepository;

    @Autowired
    private DiemRenLuyenChiTietRepository diemChiTietRepository;
    
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

        // Tìm hoặc tạo DiemRenLuyen
        DiemRenLuyen diemRenLuyen = diemRenLuyenRepository.findBySinhVienAndHkNh(sinhVien, hkNh);
        if (diemRenLuyen == null) {
            diemRenLuyen = new DiemRenLuyen();
            diemRenLuyen.setSinhVien(sinhVien);
            diemRenLuyen.setHkNh(hkNh);
            diemRenLuyen.setDiemTong(0);
            diemRenLuyen.setActive(true);
            diemRenLuyen.setCreatedDate(LocalDateTime.now());
            diemRenLuyen.setUpdatedDate(LocalDateTime.now());
            diemRenLuyenRepository.saveDiemRenLuyen(diemRenLuyen); // SỬA: Lưu trước để có ID
        }

        // Kiểm tra điểm tối đa của điều
        List<DiemRenLuyenChiTiet> chiTiets = diemChiTietRepository.getDiemRenLuyenChiTietByDiemRenLuyenId(
            diemRenLuyen.getId(), new HashMap<>()
        );
        int tongDiemHienTai = chiTiets.stream()
            .filter(ct -> ct.getDieu().getId().equals(dieu.getId()))
            .mapToInt(DiemRenLuyenChiTiet::getDiem)
            .sum();

        // Tính số điểm có thể cộng thêm
        int diemConLai = dieu.getDiemToiDa() - tongDiemHienTai;
        if (diemConLai <= 0) {
            return; // Không cộng điểm nếu đã đạt tối đa
        }

        // Chỉ cộng số điểm tối đa có thể
        int diemThucTe = Math.min(diem, diemConLai);

        // Cộng điểm
        diemRenLuyen.setDiemTong(diemRenLuyen.getDiemTong() + diemThucTe);
        diemRenLuyen.setUpdatedDate(LocalDateTime.now());
        diemRenLuyenRepository.saveDiemRenLuyen(diemRenLuyen);

        // Tạo DiemRenLuyenChiTiet
        DiemRenLuyenChiTiet chiTiet = new DiemRenLuyenChiTiet();
        chiTiet.setDiemRenLuyen(diemRenLuyen);
        chiTiet.setDieu(dieu);
        chiTiet.setDiem(diemThucTe);
        chiTiet.setActive(true);
        diemChiTietRepository.saveDiemRenLuyenChiTiet(chiTiet);
    }
}
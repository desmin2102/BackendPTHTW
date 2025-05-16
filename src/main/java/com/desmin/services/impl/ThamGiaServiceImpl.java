package com.desmin.services.impl;

import com.desmin.pojo.HoatDongNgoaiKhoa;
import com.desmin.pojo.ThamGia;
import com.desmin.pojo.User;
import com.desmin.repositories.HoatDongNgoaiKhoaRepository;
import com.desmin.repositories.ThamGiaRepository;
import com.desmin.repositories.UserRepository;
import com.desmin.services.DiemRenLuyenService;
import com.desmin.services.ThamGiaService;
import java.util.Arrays;
import java.util.HashMap;
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

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DiemRenLuyenService diemRenLuyenService; // Gọi Service để cộng điểm

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
    public void diemDanhHoatDong(User sinhVien, HoatDongNgoaiKhoa hoatDong) {
        // Gọi Repository để điểm danh
        thamGiaRepository.diemDanhHoatDong(sinhVien, hoatDong);

        // === ĐÁNH DẤU: Xử lý cộng điểm rèn luyện ===
        diemRenLuyenService.congDiemRenLuyen(sinhVien, hoatDong);
        // === KẾT THÚC ĐÁNH DẤU ===
    }

    @Override
    public void diemDanhByCsv(Long hoatDongId, MultipartFile file) {
        HoatDongNgoaiKhoa hoatDong = hoatDongNgoaiKhoaRepository.getHoatDongNgoaiKhoaById(hoatDongId);
        if (hoatDong == null) {
            throw new IllegalArgumentException("Hoạt động ngoại khóa không tồn tại với ID: " + hoatDongId);
        }

        // Gọi Repository để điểm danh qua CSV
        thamGiaRepository.diemDanhByCsv(hoatDong, file);

        // === ĐÁNH DẤU: Xử lý cộng điểm rèn luyện ===
        // Lấy danh sách ThamGia đã điểm danh để cộng điểm
        List<ThamGia> thamGias = thamGiaRepository.getThamGiasByHoatDongId(hoatDongId, null);
        for (ThamGia thamGia : thamGias) {
            if (thamGia.getState() == ThamGia.TrangThai.DiemDanh) {
                diemRenLuyenService.congDiemRenLuyen(thamGia.getSinhVien(), hoatDong);
            }
        }
        // === KẾT THÚC ĐÁNH DẤU ===
    }

    @Override
    public byte[] exportThamGiaToCsv(long hoatDongId) {
        // Lấy danh sách ThamGia
        List<ThamGia> thamGias = thamGiaRepository.getThamGiasByHoatDongId(hoatDongId, new HashMap<>());
        if (thamGias.isEmpty()) {
            throw new IllegalStateException("Không có sinh viên tham gia hoạt động này.");
        }

        // Tạo CSV
        StringBuilder csvContent = new StringBuilder();
        csvContent.append("\uFEFF"); // Thêm BOM để hỗ trợ UTF-8
        csvContent.append("id,mssv,ho,ten,diemDanh\n");

        for (ThamGia tg : thamGias) {
            String diemDanh = tg.getState() == ThamGia.TrangThai.DiemDanh ? "1" : "0";
            String ho = tg.getSinhVien().getHo().replace("\"", "\"\""); // Thoát dấu ngoặc kép
            String ten = tg.getSinhVien().getTen().replace("\"", "\"\"");
            csvContent.append(String.format(
                    "%d,%s,\"%s\",\"%s\",%s\n",
                    tg.getSinhVien().getId(),
                    tg.getSinhVien().getMssv(),
                    ho,
                    ten,
                    diemDanh
            ));
        }

        try {
            return csvContent.toString().getBytes("UTF-8");
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tạo file CSV: " + e.getMessage());
        }
    }

    @Override
    public ThamGia getThamGiaById(long id) {
        return this.thamGiaRepository.getThamGiaById(id);
    }

    @Override
    public List<ThamGia> getThamGiaBySinhVienWithStates(long sinhVienId, Map<String, String> params) {
        return thamGiaRepository.getThamGiaBySinhVienWithStates(sinhVienId,
                Arrays.asList(
                        ThamGia.TrangThai.DiemDanh,
                        ThamGia.TrangThai.DangKy,
                        ThamGia.TrangThai.BaoThieu
                ),
                params);
    }
}

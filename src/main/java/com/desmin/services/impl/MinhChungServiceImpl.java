package com.desmin.services.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.desmin.pojo.MinhChung;
import com.desmin.pojo.ThamGia;
import com.desmin.pojo.User;
import com.desmin.repositories.MinhChungRepository;
import com.desmin.repositories.ThamGiaRepository;
import com.desmin.services.MinhChungService;
import com.desmin.services.ThamGiaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class MinhChungServiceImpl implements MinhChungService {

    @Autowired
    private MinhChungRepository minhChungRepo;

    @Autowired
    private ThamGiaService thamGiaService;

    @Autowired
    private Cloudinary cloudinary;

    @Override
    public void addMinhChung(MinhChung minhChung, MultipartFile anhMinhChung, User user, Long thamGiaId) {
        try {
            ThamGia thamGia = thamGiaService.getThamGiaById(thamGiaId);
            if (thamGia == null) {
                throw new IllegalArgumentException("ThamGia không tồn tại");
            }
            if (!thamGia.getSinhVien().getId().equals(user.getId())) {
                throw new AccessDeniedException("Bạn không có quyền báo thiếu cho tham gia này");
            }
            if (thamGia.getState() != ThamGia.TrangThai.BaoThieu) {
                throw new IllegalStateException("Tham gia phải ở trạng thái Báo thiếu để gửi minh chứng");
            }

            minhChung.setThamGia(thamGia);

            // Xử lý ảnh minh chứng
            if (anhMinhChung != null && !anhMinhChung.isEmpty()) {
                try {
                    Map res = cloudinary.uploader().upload(anhMinhChung.getBytes(),
                            ObjectUtils.asMap("resource_type", "auto"));
                    minhChung.setAnhMinhChung(res.get("secure_url").toString());
                } catch (IOException ex) {
                    Logger.getLogger(MinhChungServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
                    throw new RuntimeException("Lỗi khi upload ảnh minh chứng: " + ex.getMessage());
                }
            }

            minhChung.setCreatedDate(LocalDateTime.now());
            minhChung.setUpdatedDate(LocalDateTime.now());

            minhChungRepo.saveMinhChung(minhChung);
        } catch (Exception ex) {
            Logger.getLogger(MinhChungServiceImpl.class.getName()).log(Level.SEVERE, "Error when creating MinhChung", ex);
            throw new RuntimeException("Lỗi khi tạo minh chứng: " + ex.getMessage(), ex);
        }
    }

    @Override
    public MinhChung getMinhChungById(Long id) {
        return minhChungRepo.findById(id);
    }

    @Override
    public List<MinhChung> getMinhChungByTrangThai(MinhChung.TrangThai trangThai, Map<String, String> params) {
        return minhChungRepo.findByTrangThai(trangThai, params);
    }

    @Override
    public List<MinhChung> getMinhChungByTrangThaiAndKhoa(MinhChung.TrangThai trangThai, Long khoaId, Map<String, String> params) {
        return minhChungRepo.findByTrangThaiAndKhoa(trangThai, khoaId, params);
    }

    @Override
    public void approveMinhChung(Long minhChungId, User user) {
        if (user.getRole() != User.Role.TRO_LY_SINH_VIEN) {
            throw new AccessDeniedException("Chỉ trợ lý sinh viên được duyệt minh chứng");
        }
        if (user.getKhoaPhuTrach() == null) {
            throw new IllegalStateException("Trợ lý sinh viên chưa được gán khoa phụ trách");
        }

        MinhChung minhChung = minhChungRepo.findById(minhChungId);
        if (minhChung == null) {
            throw new IllegalArgumentException("Minh chứng không tồn tại");
        }

        Long khoaId = minhChung.getThamGia().getSinhVien().getLop().getKhoa().getId();
        if (!khoaId.equals(user.getKhoaPhuTrach().getId())) {
            throw new AccessDeniedException("Bạn không có quyền duyệt minh chứng của khoa này");
        }

        if (minhChung.getTrangThai() != MinhChung.TrangThai.CHO_DUYET) {
            throw new IllegalStateException("Minh chứng không ở trạng thái chờ duyệt");
        }
        minhChung.setTrangThai(MinhChung.TrangThai.DA_DUYET);
        minhChung.setUpdatedDate(LocalDateTime.now());
        minhChungRepo.saveMinhChung(minhChung);

        ThamGia thamGia = minhChung.getThamGia();
        thamGia.setState(ThamGia.TrangThai.DiemDanh);
        thamGia.setUpdatedDate(LocalDateTime.now());
        thamGiaService.saveThamGia(thamGia);
    }

    @Override
    public void rejectMinhChung(Long minhChungId, User user) {
        if (user.getRole() != User.Role.TRO_LY_SINH_VIEN) {
            throw new AccessDeniedException("Chỉ trợ lý sinh viên được từ chối minh chứng");
        }
        if (user.getKhoaPhuTrach() == null) {
            throw new IllegalStateException("Trợ lý sinh viên chưa được gán khoa phụ trách");
        }

        MinhChung minhChung = minhChungRepo.findById(minhChungId);
        if (minhChung == null) {
            throw new IllegalArgumentException("Minh chứng không tồn tại");
        }

        Long khoaId = minhChung.getThamGia().getSinhVien().getLop().getKhoa().getId();
        if (!khoaId.equals(user.getKhoaPhuTrach().getId())) {
            throw new AccessDeniedException("Bạn không có quyền từ chối minh chứng của khoa này");
        }

        if (minhChung.getTrangThai() != MinhChung.TrangThai.CHO_DUYET) {
            throw new IllegalStateException("Minh chứng không ở trạng thái chờ duyệt");
        }
        minhChung.setTrangThai(MinhChung.TrangThai.TU_CHOI);
        minhChung.setUpdatedDate(LocalDateTime.now());
        minhChungRepo.saveMinhChung(minhChung);
    }
}
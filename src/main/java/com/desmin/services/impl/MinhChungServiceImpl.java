package com.desmin.services.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.desmin.pojo.HoatDongNgoaiKhoa;
import com.desmin.pojo.MinhChung;
import com.desmin.pojo.ThamGia;
import com.desmin.pojo.ThongBao;
import com.desmin.pojo.User;
import com.desmin.repositories.MinhChungRepository;
import com.desmin.repositories.ThongBaoRepository;
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
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MinhChungServiceImpl implements MinhChungService {

    @Autowired
    private MinhChungRepository minhChungRepo;
      @Autowired
    private ThongBaoRepository thongBaoRepo;

    @Autowired
    private ThamGiaService thamGiaService;

    @Autowired
    private Cloudinary cloudinary;
    
    @Autowired
    private JavaMailSender mailSender;


    @Transactional
    @Override
    public void addMinhChung(MinhChung minhChung, MultipartFile anhMinhChung, long userId, Long thamGiaId) {
        try {
            // Kiểm tra ThamGia
            ThamGia thamGia = thamGiaService.getThamGiaById(thamGiaId);
            if (thamGia == null) {
                throw new IllegalArgumentException("ThamGia không tồn tại");
            }

            // Kiểm tra quyền của userId
            if (!thamGia.getSinhVien().getId().equals(userId)) {
                throw new AccessDeniedException("Bạn không có quyền báo thiếu cho tham gia này");
            }

            // Gán ThamGia cho MinhChung
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

            // Thiết lập ngày tạo và cập nhật
            minhChung.setCreatedDate(LocalDateTime.now());
            minhChung.setUpdatedDate(LocalDateTime.now());

            // Lưu MinhChung
            minhChungRepo.saveMinhChung(minhChung);

            // Cập nhật trạng thái ThamGia thành BaoThieu sau khi lưu MinhChung thành công
            thamGia.setState(ThamGia.TrangThai.BaoThieu);
            thamGiaService.saveThamGia(thamGia);

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
    public void approveMinhChung(Long minhChungId) {
        MinhChung minhChung = minhChungRepo.findById(minhChungId);
        if (minhChung == null) {
            throw new IllegalArgumentException("Minh chứng không tồn tại");
        }

        if (minhChung.getTrangThai() != MinhChung.TrangThai.CHO_DUYET) {
            throw new IllegalStateException("Minh chứng không ở trạng thái chờ duyệt");
        }

        minhChung.setTrangThai(MinhChung.TrangThai.DA_DUYET);
        minhChung.setUpdatedDate(LocalDateTime.now());
        minhChungRepo.saveMinhChung(minhChung);

        ThamGia thamGia = minhChung.getThamGia();
        User sinhVien = thamGia.getSinhVien();
        HoatDongNgoaiKhoa hoatDong = thamGia.getHoatDongNgoaiKhoa();
        thamGiaService.diemDanhHoatDong(sinhVien, hoatDong);
    }

     @Override
    public void rejectMinhChung(Long minhChungId, String lyDoTuChoi) {
        MinhChung minhChung = minhChungRepo.findById(minhChungId);
        if (minhChung == null) {
            throw new IllegalArgumentException("Minh chứng không tồn tại");
        }

        if (minhChung.getTrangThai() != MinhChung.TrangThai.CHO_DUYET) {
            throw new IllegalStateException("Minh chứng không ở trạng thái chờ duyệt");
        }

        try {
            // Lấy thông tin sinh viên và hoạt động
            ThamGia thamGia = minhChung.getThamGia();
            User sinhVien = thamGia.getSinhVien();
            HoatDongNgoaiKhoa hoatDong = thamGia.getHoatDongNgoaiKhoa();

            // Tạo nội dung thông báo
            String noiDungThongBao = String.format(
                "Minh chứng cho hoạt động '%s' của bạn đã bị từ chối. Lý do: %s",
                hoatDong.getTenHoatDong(),
                lyDoTuChoi != null && !lyDoTuChoi.trim().isEmpty() ? lyDoTuChoi : "Không được cung cấp"
            );

            // Lưu thông báo vào bảng thong_bao
            ThongBao thongBao = new ThongBao();
            thongBao.setNoiDung(noiDungThongBao);
            thongBao.setRead(false);
            thongBao.setCreatedDate(LocalDateTime.now());
            thongBao.setUser(sinhVien);
            thongBaoRepo.save(thongBao);

            // Gửi email thông báo từ chối
            if (sinhVien.getEmail() != null && !sinhVien.getEmail().isEmpty()) {
                try {
                    // Kết hợp họ và tên
                    String ho = sinhVien.getHo();
                    String ten = sinhVien.getTen();
                    String tenHienThi;
                    if (ho != null && !ho.trim().isEmpty() && ten != null && !ten.trim().isEmpty()) {
                        tenHienThi = ho + " " + ten;
                    } else if (ho != null && !ho.trim().isEmpty()) {
                        tenHienThi = ho;
                    } else if (ten != null && !ten.trim().isEmpty()) {
                        tenHienThi = ten;
                    } else {
                        tenHienThi = sinhVien.getEmail();
                    }

                    SimpleMailMessage message = new SimpleMailMessage();
                    message.setTo(sinhVien.getEmail());
                    message.setSubject("Thông báo từ chối minh chứng");
                    message.setText(String.format(
                        "Kính gửi %s,\n\n" +
                            "Minh chứng của bạn cho hoạt động '%s' đã bị từ chối.\n" +
                            "Lý do: %s\n\n" +
                            "Vui lòng kiểm tra và nộp lại minh chứng nếu cần.\n" +
                            "Trân trọng,\nHệ thống",
                        tenHienThi,
                        hoatDong.getTenHoatDong(),
                        lyDoTuChoi != null && !lyDoTuChoi.trim().isEmpty() ? lyDoTuChoi : "Không được cung cấp"
                    ));
                    mailSender.send(message);
                } catch (MailException ex) {
                    Logger.getLogger(MinhChungServiceImpl.class.getName()).log(Level.SEVERE,
                        "Lỗi khi gửi email đến " + sinhVien.getEmail(), ex);
                    // Không ném exception để tiếp tục xử lý
                }
            }

            // Cập nhật trạng thái và xóa minh chứng
            minhChung.setTrangThai(MinhChung.TrangThai.TU_CHOI);
            minhChung.setUpdatedDate(LocalDateTime.now());
            minhChungRepo.deleteMinhChung(minhChung);

        } catch (Exception ex) {
            Logger.getLogger(MinhChungServiceImpl.class.getName()).log(Level.SEVERE, "Lỗi khi từ chối minh chứng", ex);
            throw new RuntimeException("Lỗi khi từ chối minh chứng: " + ex.getMessage(), ex);
        }
    }
}

package com.desmin.repositories.impl;

import com.desmin.pojo.HoatDongNgoaiKhoa;
import com.desmin.pojo.ThamGia;
import com.desmin.pojo.User;
import com.desmin.repositories.ThamGiaRepository;
import com.desmin.repositories.UserRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.web.multipart.MultipartFile;

@Repository
@Transactional
public class ThamGiaRepositoryImpl implements ThamGiaRepository {

    private static final int PAGE_SIZE = 3;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LocalSessionFactoryBean factory;

    // Phương thức này lấy danh sách tham gia với các tham số lọc
    @Override
    public List<ThamGia> getThamGias(Map<String, String> params) {
        Session session = factory.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<ThamGia> query = builder.createQuery(ThamGia.class);
        Root<ThamGia> root = query.from(ThamGia.class);
        query.select(root);

        if (params != null) {
            List<Predicate> predicates = new ArrayList<>();

            String state = params.get("state");
            if (state != null && !state.isEmpty()) {
                predicates.add(builder.equal(root.get("state"), ThamGia.TrangThai.valueOf(state)));
            }

            query.where(predicates.toArray(new Predicate[0]));
        }

        Query<ThamGia> q = session.createQuery(query);

       

        return q.getResultList();
    }

    // Phương thức này lấy danh sách tham gia của một sinh viên
    @Override
    public List<ThamGia> getThamGiasBySinhVienId(long sinhVienId, Map<String, String> params) {
        Session session = factory.getObject().getCurrentSession();
        Query<ThamGia> query = session.createNamedQuery("ThamGia.findBySinhVienId", ThamGia.class);
        query.setParameter("sinhVienId", sinhVienId);

        

        return query.getResultList();
    }

    // Phương thức này lấy danh sách tham gia theo hoạt động ngoại khóa
    @Override
    public List<ThamGia> getThamGiasByHoatDongId(long hoatDongId, Map<String, String> params) {
        Session session = factory.getObject().getCurrentSession();
        Query<ThamGia> query = session.createNamedQuery("ThamGia.findByHoatDongNgoaiKhoaId", ThamGia.class);
        query.setParameter("hoatDongId", hoatDongId);


        return query.getResultList();
    }

    // Phương thức này tìm tham gia theo sinh viên và hoạt động
    @Override
    public ThamGia findBySinhVienAndHoatDong(User sinhVien, HoatDongNgoaiKhoa hoatDong) {
        Session session = factory.getObject().getCurrentSession();
        Query<ThamGia> query = session.createNamedQuery("ThamGia.findBySinhVienAndHoatDongNgoaiKhoa", ThamGia.class);
        query.setParameter("sinhVien", sinhVien);
        query.setParameter("hoatDong", hoatDong);
        List<ThamGia> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    // Phương thức này lưu hoặc cập nhật tham gia
    @Override
    public void saveThamGia(ThamGia thamGia) {
        Session session = factory.getObject().getCurrentSession();
        session.merge(thamGia);
        session.flush();
    }

    // Phương thức này cho phép sinh viên đăng ký hoạt động ngoại khóa
    @Override
    public void dangKyHoatDong(User sinhVien, HoatDongNgoaiKhoa hoatDong) {
        ThamGia thamGia = findBySinhVienAndHoatDong(sinhVien, hoatDong);
        if (thamGia != null) {
            throw new IllegalStateException("Sinh viên đã đăng ký hoạt động này");
        }

        thamGia = new ThamGia();
        thamGia.setSinhVien(sinhVien);
        thamGia.setHoatDongNgoaiKhoa(hoatDong);
        thamGia.setState(ThamGia.TrangThai.DangKy);
        thamGia.setCreatedDate(LocalDateTime.now());
        thamGia.setUpdatedDate(LocalDateTime.now());

        saveThamGia(thamGia);
    }

    // Phương thức này thực hiện điểm danh cho sinh viên
    @Override
    public void diemDanhHoatDong(User sinhVien, HoatDongNgoaiKhoa hoatDong) {
        ThamGia thamGia = findBySinhVienAndHoatDong(sinhVien, hoatDong);
        if (thamGia == null) {
            throw new IllegalStateException("Sinh viên chưa đăng ký hoạt động này");
        }
        if (thamGia.getState() == ThamGia.TrangThai.DiemDanh) {
            throw new IllegalStateException("Sinh viên đã được điểm danh");
        }

        thamGia.setState(ThamGia.TrangThai.DiemDanh);
        thamGia.setUpdatedDate(LocalDateTime.now());
        saveThamGia(thamGia);
    }

    @Override
    public void diemDanhByCsv(HoatDongNgoaiKhoa hoatDong, MultipartFile file) {
        Session session = factory.getObject().getCurrentSession();
        List<String> errors = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            boolean isFirstLine = true;

            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false; // Bỏ qua dòng tiêu đề
                    continue;
                }

                String[] parts = line.split(",");
                if (parts.length < 5) {
                    errors.add("Dòng không hợp lệ: " + line);
                    continue;
                }

                try {
                    Long userId = Long.parseLong(parts[0].trim()); // Cột id
                    String diemDanhStr = parts[4].trim(); // Cột diemDanh

                    // Bỏ qua nếu diemDanh = 0 (giữ trạng thái DangKy)
                    if ("0".equals(diemDanhStr)) {
                        continue;
                    }

                    // Chỉ xử lý nếu diemDanh = 1
                    if (!"1".equals(diemDanhStr)) {
                        errors.add("Trạng thái điểm danh không hợp lệ, ID: " + userId);
                        continue;
                    }

                    User sinhVien = userRepository.getUserById(userId);
                    if (sinhVien == null) {
                        errors.add("Sinh viên không tồn tại với ID: " + userId);
                        continue;
                    }

                    ThamGia thamGia = findBySinhVienAndHoatDong(sinhVien, hoatDong);
                    if (thamGia == null) {
                        errors.add("Sinh viên chưa đăng ký hoạt động, ID: " + userId);
                        continue;
                    }
                    if (thamGia.getState() == ThamGia.TrangThai.DiemDanh) {
                        errors.add("Sinh viên đã được điểm danh trong hệ thống, ID: " + userId);
                        continue;
                    }

                    thamGia.setState(ThamGia.TrangThai.DiemDanh);
                    thamGia.setUpdatedDate(LocalDateTime.now());
                    session.merge(thamGia);

                } catch (NumberFormatException e) {
                    errors.add("ID sinh viên không hợp lệ: " + parts[0]);
                }
            }

            session.flush();

            if (!errors.isEmpty()) {
                throw new IllegalStateException("Lỗi khi điểm danh: " + String.join("; ", errors));
            }

        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi xử lý file CSV: " + e.getMessage());
        }
    }

    

    @Override
    public ThamGia getThamGiaById(long id) {
        Session s = factory.getObject().getCurrentSession();
        return s.get(ThamGia.class, id);
    }
    
    
    @Override
    public List<ThamGia> getThamGiaBySinhVienWithStates(long sinhVienId, List<ThamGia.TrangThai> states, Map<String, String> params) {
        Session session = factory.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<ThamGia> query = builder.createQuery(ThamGia.class);
        Root<ThamGia> root = query.from(ThamGia.class);
        query.select(root);

        // Xây dựng điều kiện WHERE
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(builder.equal(root.get("sinhVien").get("id"), sinhVienId));
        predicates.add(builder.in(root.get("state")).value(states));
        query.where(predicates.toArray(new Predicate[0]));

        // Tạo query
        Query<ThamGia> q = session.createQuery(query);

       

        return q.getResultList();
    }
}

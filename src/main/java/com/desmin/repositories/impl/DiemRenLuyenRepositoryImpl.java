package com.desmin.repositories.impl;

import com.desmin.pojo.DiemRenLuyen;
import com.desmin.pojo.DiemRenLuyenChiTiet;
import com.desmin.pojo.Dieu;
import com.desmin.pojo.HocKyNamHoc;
import com.desmin.pojo.Lop;
import com.desmin.pojo.User;
import com.desmin.repositories.DiemRenLuyenRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.time.LocalDateTime;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
@Transactional
public class DiemRenLuyenRepositoryImpl implements DiemRenLuyenRepository {

    private static final int PAGE_SIZE = 10;

    @Autowired
    private LocalSessionFactoryBean factory;

    @Override
    public List<DiemRenLuyen> getDiemRenLuyens(Map<String, String> params) {
        Session session = factory.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<DiemRenLuyen> query = builder.createQuery(DiemRenLuyen.class);
        Root<DiemRenLuyen> root = query.from(DiemRenLuyen.class);
        Join<DiemRenLuyen, User> sinhVien = root.join("sinhVien");
        Join<User, Lop> lop = sinhVien.join("lop");

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(builder.equal(root.get("active"), true));

        if (params != null) {
            if (params.containsKey("xepLoai")) {
                predicates.add(builder.equal(root.get("xepLoai"), DiemRenLuyen.XepLoai.valueOf(params.get("xepLoai"))));
            }
            if (params.containsKey("hkNhId")) {
                predicates.add(builder.equal(root.get("hkNh").get("id"), Long.parseLong(params.get("hkNhId"))));
            }
            if (params.containsKey("lopId")) {
                predicates.add(builder.equal(lop.get("id"), Long.parseLong(params.get("lopId"))));
            }
            if (params.containsKey("khoaId")) {
                predicates.add(builder.equal(lop.get("khoa").get("id"), Long.parseLong(params.get("khoaId"))));
            }
        }

        query.where(predicates.toArray(new Predicate[0]));
        Query<DiemRenLuyen> q = session.createQuery(query);

        if (params != null && params.containsKey("page")) {
            int page = Integer.parseInt(params.get("page"));
            q.setFirstResult((page - 1) * PAGE_SIZE);
            q.setMaxResults(PAGE_SIZE);
        }

        return q.getResultList();
    }

    @Override
    public List<DiemRenLuyen> getDiemRenLuyenBySinhVienId(long userId, Map<String, String> params) {
        Session session = factory.getObject().getCurrentSession();
        Query<DiemRenLuyen> query = session.createNamedQuery("DiemRenLuyen.findBySinhVienId", DiemRenLuyen.class);
        query.setParameter("userId", userId);

        if (params != null && params.containsKey("page")) {
            int page = Integer.parseInt(params.getOrDefault("page", "1"));
            int start = (page - 1) * PAGE_SIZE;
            query.setFirstResult(start);
            query.setMaxResults(PAGE_SIZE);
        }

        return query.getResultList();
    }

    @Override
    public DiemRenLuyen findBySinhVienAndHkNh(User sinhVien, HocKyNamHoc hkNh) {
        Session session = factory.getObject().getCurrentSession();
        Query<DiemRenLuyen> query = session.createNamedQuery("DiemRenLuyen.findBySinhVienAndHkNh", DiemRenLuyen.class);
        query.setParameter("sinhVien", sinhVien);
        query.setParameter("hkNh", hkNh);
        List<DiemRenLuyen> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    public void saveDiemRenLuyen(DiemRenLuyen diemRenLuyen) {
        Session session = factory.getObject().getCurrentSession();
        session.merge(diemRenLuyen);
        session.flush();
    }
@Override
public DiemRenLuyen createDiemRenLuyen(User sinhVien, HocKyNamHoc hkNh) {
    Session s = factory.getObject().getCurrentSession();
    DiemRenLuyen diemRenLuyen = findBySinhVienAndHkNh(sinhVien, hkNh);

    if (diemRenLuyen == null) {
        diemRenLuyen = new DiemRenLuyen();
        diemRenLuyen.setSinhVien(sinhVien);
        diemRenLuyen.setHkNh(hkNh);
        diemRenLuyen.setDiemTong(0);
        diemRenLuyen.setActive(true);
        diemRenLuyen.setCreatedDate(LocalDateTime.now());
        diemRenLuyen.setUpdatedDate(LocalDateTime.now());

        // Đồng bộ quan hệ hai chiều
        if (sinhVien.getDiemRenLuyenList() == null) {
            sinhVien.setDiemRenLuyenList(new ArrayList<>());
        }
        sinhVien.getDiemRenLuyenList().add(diemRenLuyen);

        s.persist(diemRenLuyen);
    }

    return diemRenLuyen;
}
@Override
    @Transactional(readOnly = true)
    public List<DiemRenLuyen> getDiemRenLuyenTongHop(Long khoaId, Long lopId, String xepLoai, Long hkNhId, int page, int size) {
        try {
            Session session = factory.getObject().getCurrentSession();
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<DiemRenLuyen> query = builder.createQuery(DiemRenLuyen.class);
            Root<DiemRenLuyen> root = query.from(DiemRenLuyen.class);
            Join<DiemRenLuyen, User> sinhVien = root.join("sinhVien");
            Join<User, Lop> lop = sinhVien.join("lop");
            Join<Lop, com.desmin.pojo.Khoa> khoa = lop.join("khoa");
            Join<DiemRenLuyen, HocKyNamHoc> hkNh = root.join("hkNh");

            List<Predicate> predicates = new ArrayList<>();
            predicates.add(builder.equal(root.get("active"), true));

            if (khoaId != null) {
                predicates.add(builder.equal(khoa.get("id"), khoaId));
            }
            if (lopId != null) {
                predicates.add(builder.equal(lop.get("id"), lopId));
            }
            if (xepLoai != null && !xepLoai.isEmpty()) {
                try {
                    predicates.add(builder.equal(root.get("xepLoai"), DiemRenLuyen.XepLoai.valueOf(xepLoai)));
                } catch (IllegalArgumentException e) {
                    return new ArrayList<>(); // Trả về rỗng nếu xepLoai không hợp lệ
                }
            }
            if (hkNhId != null) {
                predicates.add(builder.equal(hkNh.get("id"), hkNhId));
            }

            query.where(predicates.toArray(new Predicate[0]));
            query.orderBy(builder.asc(sinhVien.get("id")), builder.asc(hkNh.get("id")));

            Query<DiemRenLuyen> q = session.createQuery(query);
            q.setFirstResult((page - 1) * size);
            q.setMaxResults(size);

            List<DiemRenLuyen> results = q.getResultList();
            return results;
        } catch (Exception e) {
            throw new RuntimeException("Error executing query: " + e.getMessage(), e);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<DiemRenLuyen> getAllDiemRenLuyenTongHop(Long khoaId, Long lopId, String xepLoai, Long hkNhId) {
        try {
            Session session = factory.getObject().getCurrentSession();
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<DiemRenLuyen> query = builder.createQuery(DiemRenLuyen.class);
            Root<DiemRenLuyen> root = query.from(DiemRenLuyen.class);
            Join<DiemRenLuyen, User> sinhVien = root.join("sinhVien");
            Join<User, Lop> lop = sinhVien.join("lop");
            Join<Lop, com.desmin.pojo.Khoa> khoa = lop.join("khoa");
            Join<DiemRenLuyen, HocKyNamHoc> hkNh = root.join("hkNh");

            List<Predicate> predicates = new ArrayList<>();
            predicates.add(builder.equal(root.get("active"), true));

            if (khoaId != null) {
                predicates.add(builder.equal(khoa.get("id"), khoaId));
            }
            if (lopId != null) {
                predicates.add(builder.equal(lop.get("id"), lopId));
            }
            if (xepLoai != null && !xepLoai.isEmpty()) {
                try {
                    predicates.add(builder.equal(root.get("xepLoai"), DiemRenLuyen.XepLoai.valueOf(xepLoai)));
                } catch (IllegalArgumentException e) {
                    return new ArrayList<>();
                }
            }
            if (hkNhId != null) {
                predicates.add(builder.equal(hkNh.get("id"), hkNhId));
            }

            query.where(predicates.toArray(new Predicate[0]));
            query.orderBy(builder.asc(sinhVien.get("id")), builder.asc(hkNh.get("id")));

            Query<DiemRenLuyen> q = session.createQuery(query);
            return q.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error executing query: " + e.getMessage(), e);
        }
    }

}
package com.desmin.repositories;

import com.desmin.pojo.DiemRenLuyen;
import com.desmin.pojo.HocKyNamHoc;
import com.desmin.pojo.User;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;

import jakarta.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.transaction.annotation.Transactional;

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

        // Join với HocKyNamHoc để lọc namHoc, hocKy
        Join<DiemRenLuyen, HocKyNamHoc> hkNhJoin = root.join("hkNh");

        query.select(root);

        if (params != null) {
            List<Predicate> predicates = new ArrayList<>();

            String namHoc = params.get("namHoc");
            if (namHoc != null && !namHoc.isEmpty()) {
                predicates.add(builder.equal(hkNhJoin.get("namHoc"), namHoc));
            }

            String hocKy = params.get("hocKy");
            if (hocKy != null && !hocKy.isEmpty()) {
                predicates.add(builder.equal(hkNhJoin.get("hocKy"), hocKy));
            }

            query.where(predicates.toArray(new Predicate[0]));
        }

        // Sắp xếp theo startDate của HocKyNamHoc (mới nhất trước)
        query.orderBy(builder.desc(hkNhJoin.get("startDate")));

        Query<DiemRenLuyen> q = session.createQuery(query);

        // Phân trang
        if (params != null && params.containsKey("page")) {
            int page = Integer.parseInt(params.getOrDefault("page", "1"));
            int start = (page - 1) * PAGE_SIZE;
            q.setFirstResult(start);
            q.setMaxResults(PAGE_SIZE);
        }

        return q.getResultList();
    }

    @Override
    public List<DiemRenLuyen> getDiemRenLuyenBySinhVienId(long userId, Map<String, String> params) {
        Session session = factory.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<DiemRenLuyen> query = builder.createQuery(DiemRenLuyen.class);
        Root<DiemRenLuyen> root = query.from(DiemRenLuyen.class);

        // Join với HocKyNamHoc để lọc namHoc, hocKy
        Join<DiemRenLuyen, HocKyNamHoc> hkNhJoin = root.join("hkNh");

        query.select(root);

        List<Predicate> predicates = new ArrayList<>();
        // Lọc theo userId
        predicates.add(builder.equal(root.get("sinhVien").get("id"), userId));

        if (params != null) {
            String namHoc = params.get("namHoc");
            if (namHoc != null && !namHoc.isEmpty()) {
                predicates.add(builder.equal(hkNhJoin.get("namHoc"), namHoc));
            }

            String hocKy = params.get("hocKy");
            if (hocKy != null && !hocKy.isEmpty()) {
                predicates.add(builder.equal(hkNhJoin.get("hocKy"), hocKy));
            }
        }

        query.where(predicates.toArray(new Predicate[0]));

        // Sắp xếp theo startDate của HocKyNamHoc (mới nhất trước)
        query.orderBy(builder.desc(hkNhJoin.get("startDate")));

        Query<DiemRenLuyen> q = session.createQuery(query);

        // Phân trang
        if (params != null && params.containsKey("page")) {
            int page = Integer.parseInt(params.getOrDefault("page", "1"));
            int start = (page - 1) * PAGE_SIZE;
            q.setFirstResult(start);
            q.setMaxResults(PAGE_SIZE);
        }

        return q.getResultList();
    }

    @Override
    public void saveDiemRenLuyen(DiemRenLuyen diemRenLuyen) {
        Session session = factory.getObject().getCurrentSession();
        session.merge(diemRenLuyen);
        session.flush();
    }
    
    @Override
    /** Bổ sung: Triển khai findBySinhVienAndHkNh */
    public DiemRenLuyen findBySinhVienAndHkNh(User sinhVien, HocKyNamHoc hkNh) {
        Session session = factory.getObject().getCurrentSession();
        Query query = session.createNamedQuery("DiemRenLuyen.findBySinhVienAndHkNh", DiemRenLuyen.class);
        query.setParameter("sinhVien", sinhVien);
        query.setParameter("hkNh", hkNh);
        List<DiemRenLuyen> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }
}

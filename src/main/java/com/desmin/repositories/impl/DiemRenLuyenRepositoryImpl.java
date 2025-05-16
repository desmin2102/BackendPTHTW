package com.desmin.repositories.impl;

import com.desmin.pojo.DiemRenLuyen;
import com.desmin.pojo.HocKyNamHoc;
import com.desmin.pojo.User;
import com.desmin.repositories.DiemRenLuyenRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
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
        query.select(root);

        if (params != null) {
            List<Predicate> predicates = new ArrayList<>();

            String xepLoai = params.get("xepLoai");
            if (xepLoai != null && !xepLoai.isEmpty()) {
                predicates.add(builder.equal(root.get("xepLoai"), DiemRenLuyen.XepLoai.valueOf(xepLoai)));
            }

            String hkNhId = params.get("hkNhId");
            if (hkNhId != null && !hkNhId.isEmpty()) {
                predicates.add(builder.equal(root.get("hkNh").get("id"), Long.parseLong(hkNhId)));
            }

            query.where(predicates.toArray(new Predicate[0]));
        }

        Query<DiemRenLuyen> q = session.createQuery(query);

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

}
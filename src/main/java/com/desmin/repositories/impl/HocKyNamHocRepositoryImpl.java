/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.desmin.repositories.impl;

import com.desmin.pojo.HocKyNamHoc;
import com.desmin.repositories.HocKyNamHocRepository;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author ADMIN
 */
@Repository
@Transactional
public class HocKyNamHocRepositoryImpl implements HocKyNamHocRepository {

    @Autowired
    private LocalSessionFactoryBean factory;

    @Override
    public List<HocKyNamHoc> getHocKyNamHocs(Map<String, String> params) {
        Session s = factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<HocKyNamHoc> q = b.createQuery(HocKyNamHoc.class);
        Root<HocKyNamHoc> root = q.from(HocKyNamHoc.class);
        q.select(root);

        Query query = s.createQuery(q);
        return query.getResultList();
    }

    @Override
    public HocKyNamHoc getHocKyNamHocById(long id) {
        Session s = factory.getObject().getCurrentSession();
        return s.get(HocKyNamHoc.class, id);
    }

    
    @Override
    public HocKyNamHoc findCurrentHocKyNamHoc() {
        Session s = factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<HocKyNamHoc> q = b.createQuery(HocKyNamHoc.class);
        Root<HocKyNamHoc> root = q.from(HocKyNamHoc.class);
        LocalDate now = LocalDate.now();
        q.select(root)
         .where(
             b.and(
                 b.lessThanOrEqualTo(root.get("startDate"), now),
                 b.greaterThanOrEqualTo(root.get("endDate"), now),
                 b.equal(root.get("active"), true)
             )
         )
         .orderBy(b.desc(root.get("id"))); // Ưu tiên học kỳ mới nhất nếu trùng
        return s.createQuery(q).setMaxResults(1).uniqueResult();
    }
    
    @Override
    public void createHocKyNamHoc(HocKyNamHoc hocKyNamHoc) {
        Session s = factory.getObject().getCurrentSession();
        s.persist(hocKyNamHoc);
    }
    
    @Override
    public List<HocKyNamHoc> findOverlappingHocKyNamHoc(LocalDate startDate, LocalDate endDate) {
        Session s = factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<HocKyNamHoc> q = b.createQuery(HocKyNamHoc.class);
        Root<HocKyNamHoc> root = q.from(HocKyNamHoc.class);
        q.select(root).where(
            b.and(
                b.equal(root.get("active"), true),
                b.or(
                    b.between(root.get("startDate"), startDate, endDate),
                    b.between(root.get("endDate"), startDate, endDate),
                    b.and(
                        b.lessThanOrEqualTo(root.get("startDate"), startDate),
                        b.greaterThanOrEqualTo(root.get("endDate"), endDate)
                    )
                )
            )
        );
        return s.createQuery(q).getResultList();
    }

    @Override
    public boolean existsByHocKyAndNamHoc(HocKyNamHoc.HocKy hocKy, String namHoc) {
        Session s = factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<HocKyNamHoc> q = b.createQuery(HocKyNamHoc.class);
        Root<HocKyNamHoc> root = q.from(HocKyNamHoc.class);
        q.select(root).where(
            b.and(
                b.equal(root.get("hocKy"), hocKy),
                b.equal(root.get("namHoc"), namHoc)
            )
        );
        return s.createQuery(q).uniqueResult() != null;
    }
}

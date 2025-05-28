/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.desmin.repositories.impl;

import com.desmin.pojo.HoatDongNgoaiKhoa;
import com.desmin.pojo.User;
import com.desmin.repositories.HoatDongNgoaiKhoaRepository;
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
public class HoatDongNgoaiKhoaRepositoryImpl implements HoatDongNgoaiKhoaRepository {

    private static final int PAGE_SIZE = 6;

    @Autowired
    private LocalSessionFactoryBean factory;

  @Override
    public List<HoatDongNgoaiKhoa> getHoatDongNgoaiKhoas(Map<String, String> params) {
        Session s = factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<HoatDongNgoaiKhoa> q = b.createQuery(HoatDongNgoaiKhoa.class);
        Root<HoatDongNgoaiKhoa> root = q.from(HoatDongNgoaiKhoa.class);
        q.select(root);

        Query query = s.createQuery(q);

        // Kiểm tra nếu params chứa "noPaging=true" thì không áp dụng phân trang
        boolean noPaging = params != null && "true".equalsIgnoreCase(params.getOrDefault("noPaging", "false"));
        if (!noPaging) {
            // Áp dụng phân trang
            if (params != null && params.containsKey("page")) {
                try {
                    int page = Integer.parseInt(params.getOrDefault("page", "1"));
                    if (page < 1) {
                        page = 1; // Đảm bảo page không nhỏ hơn 1
                    }
                    int start = (page - 1) * PAGE_SIZE;
                    query.setMaxResults(PAGE_SIZE);
                    query.setFirstResult(start);
                } catch (NumberFormatException e) {
                    // Nếu page không phải số hợp lệ, lấy trang 1
                    query.setMaxResults(PAGE_SIZE);
                    query.setFirstResult(0);
                }
            } else {
                // Mặc định lấy trang 1 nếu không có tham số page
                query.setMaxResults(PAGE_SIZE);
                query.setFirstResult(0);
            }
        }

        return query.getResultList();
    }
    @Transactional
    @Override
    public HoatDongNgoaiKhoa getHoatDongNgoaiKhoaById(long id) {
        Session s = factory.getObject().getCurrentSession();
        return s.get(HoatDongNgoaiKhoa.class, id);
    }

    @Override
    public List<HoatDongNgoaiKhoa> findByHanDangKyBefore(LocalDate date) {
        Session session = factory.getObject().getCurrentSession();
        Query query = session.createNamedQuery("HoatDongNgoaiKhoa.findByHanDangKyBefore", HoatDongNgoaiKhoa.class);
        query.setParameter("date", date);
        return query.getResultList();
    }

    @Override
    public List<HoatDongNgoaiKhoa> findActiveAndNotExpired(LocalDate date) {
        Session session = factory.getObject().getCurrentSession();
        Query query = session.createNamedQuery("HoatDongNgoaiKhoa.findActiveAndNotExpired", HoatDongNgoaiKhoa.class);
        query.setParameter("date", date);
        return query.getResultList();
    }

    @Override
    public void update(HoatDongNgoaiKhoa hoatDong) {
        Session session = factory.getObject().getCurrentSession();
        session.merge(hoatDong);
        session.flush();
    }

   

    @Override
    public HoatDongNgoaiKhoa addHoatDongNgoaiKhoa(HoatDongNgoaiKhoa h) {
        Session s = this.factory.getObject().getCurrentSession();
        s.persist(h);

        return h;
    }


}

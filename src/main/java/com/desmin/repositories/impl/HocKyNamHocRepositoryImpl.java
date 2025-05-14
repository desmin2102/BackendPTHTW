/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.desmin.repositories.impl;

import com.desmin.pojo.HoatDongNgoaiKhoa;
import com.desmin.pojo.HocKyNamHoc;
import com.desmin.pojo.Khoa;
import com.desmin.repositories.HocKyNamHocRepository;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
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

}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.desmin.repositories.impl;

import com.desmin.pojo.BaiViet;
import com.desmin.pojo.Khoa;
import com.desmin.repositories.BaiVietRepository;
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
public class BaiVietRepositoryImpl implements BaiVietRepository {

    @Autowired
    private LocalSessionFactoryBean factory;

    @Override
    public List<BaiViet> getAllBaiViet() {
        Session session = factory.getObject().getCurrentSession();
        Query query = session.createNamedQuery("BaiViet.findAll", BaiViet.class);
        return query.getResultList();
    }

    @Override
    public BaiViet getBaiVietById(long id) {
        Session s = factory.getObject().getCurrentSession();
        return s.get(BaiViet.class, id);
    }

    @Override
    public BaiViet addBaiViet(BaiViet baiViet) {
        Session session = factory.getObject().getCurrentSession();
       session.persist(baiViet);

        return baiViet;
    }

    @Override
    public void updateBaiViet(BaiViet baiViet) {
        Session session = factory.getObject().getCurrentSession();
        session.merge(baiViet);
        session.flush();
    }

    @Override
    public void deleteBaiViet(long id) {
        Session session = factory.getObject().getCurrentSession();
        BaiViet baiViet = getBaiVietById(id);
        if (baiViet != null) {
            session.remove(baiViet);
            session.flush();
        }
    }

    @Override
    public BaiViet getBaiVietByHoatDongId(long hoatDongId) {
        Session session = factory.getObject().getCurrentSession();
        Query query = session.createNamedQuery("BaiViet.findBaiVietByHoatDongId", BaiViet.class);
        query.setParameter("hoatDongId", hoatDongId);
        return (BaiViet) query.getSingleResult();
        
    }
}

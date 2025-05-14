/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.desmin.repositories.impl;

import com.desmin.pojo.DiemRenLuyenChiTiet;
import com.desmin.pojo.Dieu;
import com.desmin.repositories.DiemRenLuyenChiTietRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import java.util.List;
import java.util.Map;
import org.hibernate.Session;
import org.hibernate.query.Query;
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
public class DiemRenLuyenChiTietRepositoryImpl implements DiemRenLuyenChiTietRepository {

    @Autowired
    private LocalSessionFactoryBean factory;

    @Override
    public List<DiemRenLuyenChiTiet> getDiemRenLuyenChiTiets(Map<String, String> params) {
        Session s = factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<DiemRenLuyenChiTiet> q = b.createQuery(DiemRenLuyenChiTiet.class);
        Root<DiemRenLuyenChiTiet> root = q.from(DiemRenLuyenChiTiet.class);
        q.select(root);

        Query query = s.createQuery(q);
        return query.getResultList();
    }

    @Override
    public List<DiemRenLuyenChiTiet> getDiemRenLuyenChiTietByDiemRenLuyenId(long drlId, Map<String, String> params) {
        Session session = factory.getObject().getCurrentSession();
        Query<DiemRenLuyenChiTiet> query = session.createNamedQuery(
                "DiemRenLuyenChiTiet.findByDiemRenLuyenId", DiemRenLuyenChiTiet.class
        );
        query.setParameter("diemRenLuyenId", drlId);

        return query.getResultList();
    }

    @Override
    public void saveDiemRenLuyenChiTiet(DiemRenLuyenChiTiet chiTiet) {
        Session session = factory.getObject().getCurrentSession();
        session.merge(chiTiet);
        session.flush();
    }


}

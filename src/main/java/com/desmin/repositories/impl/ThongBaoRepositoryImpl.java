/*
 * Click nb://fs://SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nb://fs://SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.desmin.repositories.impl;

import com.desmin.pojo.ThongBao;
import com.desmin.pojo.User;
import com.desmin.repositories.ThongBaoRepository;
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
public class ThongBaoRepositoryImpl implements ThongBaoRepository {

    @Autowired
    private LocalSessionFactoryBean factory;

    @Override
    public List<ThongBao> getThongBaos(Map<String, String> params) {
        Session s = factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<ThongBao> q = b.createQuery(ThongBao.class);
        Root<ThongBao> root = q.from(ThongBao.class);
        q.select(root);

        Query query = s.createQuery(q);
        return query.getResultList();
    }

    @Override
    public ThongBao getThongBaoById(long id) {
        Session s = factory.getObject().getCurrentSession();
        return s.get(ThongBao.class, id);
    }

    @Override
    public List<ThongBao> getThongBaosByUserOrPublic(User user) {
        Session s = factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<ThongBao> q = b.createQuery(ThongBao.class);
        Root<ThongBao> root = q.from(ThongBao.class);
        q.select(root).where(
                b.or(
                        b.equal(root.get("user"), user),
                        b.isNull(root.get("user"))
                )
        );
        Query query = s.createQuery(q);
        return query.getResultList();
    }

    @Override
    public void save(ThongBao thongBao) {
        Session s = factory.getObject().getCurrentSession();

        if (thongBao.getId() == null) {
            s.persist(thongBao);  // thêm mới
        } else {
            s.merge(thongBao);    // cập nhật
        }
    }

}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.desmin.repositories.impl;

import com.desmin.pojo.BaiViet;
import com.desmin.pojo.Comment;
import com.desmin.pojo.Like;
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

    private static final int PAGE_SIZE = 6;

    @Autowired
    private LocalSessionFactoryBean factory;

    @Override
    public List<BaiViet> getAllBaiViet(Map<String, String> params) {
        Session s = factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<BaiViet> q = b.createQuery(BaiViet.class);
        Root<BaiViet> root = q.from(BaiViet.class);
        q.select(root);

        Query query = s.createQuery(q);

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
@Override
public List<Comment> getComments(long baivietId) {
    Session s = this.factory.getObject().getCurrentSession();
    CriteriaBuilder b = s.getCriteriaBuilder();
    CriteriaQuery<Comment> q = b.createQuery(Comment.class);
    Root<Comment> root = q.from(Comment.class);
    q.select(root);
    q.where(b.equal(root.get("baiViet").get("id"), baivietId));
    Query query = s.createQuery(q);
    return query.getResultList();
}

     @Override
    public List<Like> getLikes(long baivietId) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Like> q = b.createQuery(Like.class);
        Root root = q.from(Like.class);
        q.select(root);
    q.where(b.equal(root.get("baiViet").get("id"), baivietId));
        Query query = s.createQuery(q);
        return query.getResultList();
    }


}

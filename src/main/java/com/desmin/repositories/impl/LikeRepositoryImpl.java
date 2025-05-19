/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.desmin.repositories.impl;

import com.desmin.pojo.Like;
import com.desmin.repositories.LikeRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
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
public class LikeRepositoryImpl implements LikeRepository {

    @Autowired
    private LocalSessionFactoryBean factory;

    @Override
    public void addLike(Like like) {
        Session session = factory.getObject().getCurrentSession();
        session.persist(like); // Thay save bằng persist để rõ ngữ nghĩa
    }

@Override
public void removeLike(Like like) {
    Session session = factory.getObject().getCurrentSession();

    // Dùng criteria để tìm chính xác Like cần xóa
    CriteriaBuilder cb = session.getCriteriaBuilder();
    CriteriaQuery<Like> cq = cb.createQuery(Like.class);
    Root<Like> root = cq.from(Like.class);

    cq.select(root).where(
        cb.equal(root.get("user").get("id"), like.getUser().getId()),
        cb.equal(root.get("baiViet").get("id"), like.getBaiViet().getId())
    );

    Like existingLike = session.createQuery(cq).uniqueResult();

    if (existingLike != null) {
        session.remove(existingLike);
        session.flush();
    }
}


    @Override
public boolean existsLike(long userId, long baiVietId) {
    Session session = factory.getObject().getCurrentSession();
    CriteriaBuilder cb = session.getCriteriaBuilder();
    CriteriaQuery<Long> cq = cb.createQuery(Long.class);
    Root<Like> likeRoot = cq.from(Like.class);

    cq.select(cb.count(likeRoot));
    cq.where(
        cb.and(
            cb.equal(likeRoot.get("user").get("id"), userId),
            cb.equal(likeRoot.get("baiViet").get("id"), baiVietId)
        )
    );

    Long count = session.createQuery(cq).getSingleResult();
    return count != null && count > 0;
}

   
}
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.desmin.repositories.impl;

import com.desmin.pojo.User;
import jakarta.persistence.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.desmin.repositories.UserRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author ADMIN
 */
@Repository
@Transactional
public class UserRepositoryImpl implements UserRepository {

    
    @Autowired
    private LocalSessionFactoryBean factory;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public User getUserByUsername(String username) {
        Session s = this.factory.getObject().getCurrentSession();
        Query q = s.createNamedQuery("User.findByUsername", User.class);
        q.setParameter("username", username);
        return (User) q.getSingleResult();

    }

    @Override
    public User addUser(User tk) {
        Session s = this.factory.getObject().getCurrentSession();
        s.persist(tk);

        return tk;
    }

    @Override
    public boolean authenticate(String username, String password) {
        User u = this.getUserByUsername(username);

        return this.passwordEncoder.matches(password, u.getPassword());
    }

    @Override
    public User getUserById(long id) {
        Session s = factory.getObject().getCurrentSession();
        return s.get(User.class, id);
    }
    
       @Override
    public List<User> findAllSinhVien() {
        Session s = factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<User> q = b.createQuery(User.class);
        Root<User> root = q.from(User.class);
        q.select(root).where(b.equal(root.get("role"), User.Role.SINH_VIEN));
        return s.createQuery(q).getResultList();
    }

@Override
public List<User> getAllSinhVien(Map<String, String> params) {
    Session s = factory.getObject().getCurrentSession();
    CriteriaBuilder b = s.getCriteriaBuilder();
    CriteriaQuery<User> q = b.createQuery(User.class);
    Root<User> root = q.from(User.class);
    q.select(root);

    if (params != null) {
        List<Predicate> predicates = new ArrayList<>();

        // Luôn lọc theo vai trò là SINH_VIEN
        predicates.add(b.equal(root.get("role"), User.Role.SINH_VIEN));

        String lopId = params.get("lopId");
        if (lopId != null && !lopId.isEmpty()) {
            try {
                predicates.add(b.equal(root.get("lop").get("id"), Integer.parseInt(lopId)));
            } catch (NumberFormatException e) {
                // Bỏ qua nếu lỗi format
            }
        }

        String khoaId = params.get("khoaId");
        if (khoaId != null && !khoaId.isEmpty()) {
            try {
                predicates.add(b.equal(root.get("lop").get("khoa").get("id"), Integer.parseInt(khoaId)));
            } catch (NumberFormatException e) {
                // Bỏ qua nếu lỗi format
            }
        }

        String kw = params.get("kw");
        if (kw != null && !kw.isEmpty()) {
            predicates.add(b.or(
                b.like(root.get("ho"), "%" + kw + "%"),
                b.like(root.get("ten"), "%" + kw + "%"),
                b.like(root.get("mssv"), "%" + kw + "%")
            ));
        }

        q.where(predicates.toArray(Predicate[]::new));
    }

    Query query = s.createQuery(q);

   

    return query.getResultList();
}

   @Override
    public List<User> getTroLysByKhoaId(long khoaId) { // Sửa để trả về List<User>
        Session s = factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<User> q = b.createQuery(User.class);
        Root<User> root = q.from(User.class);

        // Lọc trợ lý (role = TRO_LY) và khoaPhuTrach.id = khoaId
        q.select(root)
         .where(
             b.and(
                 b.equal(root.get("role"), User.Role.TRO_LY_SINH_VIEN),
                 b.equal(root.get("khoaPhuTrach").get("id"), khoaId) 
             )
         );

        return s.createQuery(q).getResultList();
    }

}

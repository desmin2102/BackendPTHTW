package com.desmin.repositories.impl;

import com.desmin.pojo.Khoa;
import com.desmin.pojo.Lop;
import com.desmin.repositories.LopRepository;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class LopRepositoryImpl implements LopRepository {

    @Autowired
    private LocalSessionFactoryBean factory;

    @Override
    public List<Lop> getLops(Map<String, String> params) {
        Session s = factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Lop> q = b.createQuery(Lop.class);
        Root<Lop> root = q.from(Lop.class);
        q.select(root);

        // Xử lý điều kiện lọc (nếu có)
        List<Predicate> predicates = new ArrayList<>();
        if (params != null) {
            String kw = params.get("kw");
            if (kw != null && !kw.isEmpty()) {
                predicates.add(b.like(root.get("tenKhoa"), "%" + kw + "%"));
            }
        }

        if (!predicates.isEmpty()) {
            q.where(predicates.toArray(new Predicate[0]));
        }

        Query query = s.createQuery(q);
        return query.getResultList();    }

    @Override
    public Lop getLopById(long id) {
        Session s = factory.getObject().getCurrentSession();
        return s.get(Lop.class, id);
    }
}

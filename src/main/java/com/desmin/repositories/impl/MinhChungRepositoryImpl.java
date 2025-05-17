package com.desmin.repositories.impl;

import com.desmin.pojo.MinhChung;
import com.desmin.repositories.MinhChungRepository;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.Query;
import java.util.List;
import java.util.Map;

@Repository
@Transactional
public class MinhChungRepositoryImpl implements MinhChungRepository {

    private static final int PAGE_SIZE = 10;

    @Autowired
    private LocalSessionFactoryBean factory;

    @Override
    public void saveMinhChung(MinhChung minhChung) {
        Session session = factory.getObject().getCurrentSession();
        session.merge(minhChung);
        session.flush();
    }

    @Override
    public MinhChung findById(Long id) {
        Session session = factory.getObject().getCurrentSession();
        Query query = session.createNamedQuery("MinhChung.findById", MinhChung.class);
        query.setParameter("id", id);
        List<MinhChung> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    public List<MinhChung> findByTrangThai(MinhChung.TrangThai trangThai, Map<String, String> params) {
        Session session = factory.getObject().getCurrentSession();
        Query query = session.createNamedQuery("MinhChung.findByTrangThai", MinhChung.class);
        query.setParameter("trangThai", trangThai);

        // Phân trang
        if (params != null && params.containsKey("page")) {
            int page = Integer.parseInt(params.getOrDefault("page", "1"));
            int start = (page - 1) * PAGE_SIZE;
            query.setFirstResult(start);
            query.setMaxResults(PAGE_SIZE);
        }

        return query.getResultList();
    }

    @Override
    public List<MinhChung> findByTrangThaiAndKhoa(MinhChung.TrangThai trangThai, Long khoaId, Map<String, String> params) {
        Session session = factory.getObject().getCurrentSession();
        Query query = session.createNamedQuery("MinhChung.findByTrangThaiAndKhoa", MinhChung.class);
        query.setParameter("trangThai", trangThai);
        query.setParameter("khoaId", khoaId);

        // Phân trang
        if (params != null && params.containsKey("page")) {
            int page = Integer.parseInt(params.getOrDefault("page", "1"));
            int start = (page - 1) * PAGE_SIZE;
            query.setFirstResult(start);
            query.setMaxResults(PAGE_SIZE);
        }

        return query.getResultList();
    }

    @Override
    public void deleteMinhChung(MinhChung minhChung) {
        Session session = factory.getObject().getCurrentSession();
        session.remove(minhChung);
        session.flush();
    }

}

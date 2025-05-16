package com.desmin.repositories.impl;

import com.desmin.pojo.DiemRenLuyen;
import com.desmin.pojo.DiemRenLuyenChiTiet;
import com.desmin.pojo.Dieu;
import com.desmin.repositories.DiemRenLuyenChiTietRepository;
import com.desmin.repositories.DieuRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Transactional
public class DiemRenLuyenChiTietRepositoryImpl implements DiemRenLuyenChiTietRepository {

     @Autowired
    private DieuRepository dieuRepo;
    @Autowired
    private LocalSessionFactoryBean factory;

    @Override
    public List<DiemRenLuyenChiTiet> getDiemRenLuyenChiTietByDiemRenLuyenId(Long diemRenLuyenId, Map<String, String> params) {
        Session session = factory.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<DiemRenLuyenChiTiet> query = builder.createQuery(DiemRenLuyenChiTiet.class);
        Root<DiemRenLuyenChiTiet> root = query.from(DiemRenLuyenChiTiet.class);
        query.select(root);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(builder.equal(root.get("diemRenLuyen").get("id"), diemRenLuyenId));
        query.where(predicates.toArray(new Predicate[0]));

        Query<DiemRenLuyenChiTiet> q = session.createQuery(query);
        return q.getResultList();
    }

    @Override
    public void saveDiemRenLuyenChiTiet(DiemRenLuyenChiTiet chiTiet) {
        Session session = factory.getObject().getCurrentSession();
        session.merge(chiTiet);
        session.flush();
    }
    @Override
    public void createDiemRenLuyenChiTietForAllDieu(DiemRenLuyen diemRenLuyen) {
        Session s = factory.getObject().getCurrentSession();
        List<DiemRenLuyenChiTiet> existingChiTiets = getDiemRenLuyenChiTietByDiemRenLuyenId(diemRenLuyen.getId(), new HashMap<>());
        if (existingChiTiets.isEmpty()) {
            List<Dieu> allDieu = dieuRepo.getDieus(null);
            for (Dieu d : allDieu) {
                DiemRenLuyenChiTiet chiTiet = new DiemRenLuyenChiTiet();
                chiTiet.setDiemRenLuyen(diemRenLuyen);
                chiTiet.setDieu(d);
                chiTiet.setDiem(0);
                chiTiet.setActive(true);
            
                s.persist(chiTiet);
            }
        }
    }
}
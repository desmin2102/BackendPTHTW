package com.desmin.repositories.impl;

import com.desmin.pojo.ChatRoom;
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
import java.util.List;

@Repository
@Transactional
public class ChatRoomRepositoryImpl implements com.desmin.repositories.ChatRoomRepository {

    @Autowired
    private LocalSessionFactoryBean factory;

    @Override
    public ChatRoom save(ChatRoom chatRoom) {
        Session session = factory.getObject().getCurrentSession();
        if (chatRoom.getId() == null) {
            session.persist(chatRoom);
        } else {
            session.merge(chatRoom);
        }
        session.flush();
        return chatRoom;
    }

    @Override
    public List<ChatRoom> findByParticipantId(Long userId) {
        Session session = factory.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<ChatRoom> query = builder.createQuery(ChatRoom.class);
        Root<ChatRoom> root = query.from(ChatRoom.class);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(builder.equal(root.get("sinhVien").get("id"), userId));

        query.where(predicates.toArray(new Predicate[0]));
        Query<ChatRoom> q = session.createQuery(query);
        return q.getResultList();
    }

    @Override
    public ChatRoom findById(Long id) {
        Session session = factory.getObject().getCurrentSession();
        return session.get(ChatRoom.class, id);
    }

    @Override
    public ChatRoom findByKhoaIdAndSinhVienId(Long khoaId, Long sinhVienId) {
        Session session = factory.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<ChatRoom> query = builder.createQuery(ChatRoom.class);
        Root<ChatRoom> root = query.from(ChatRoom.class);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(builder.equal(root.get("khoa").get("id"), khoaId));
        predicates.add(builder.equal(root.get("sinhVien").get("id"), sinhVienId));

        query.where(predicates.toArray(new Predicate[0]));
        Query<ChatRoom> q = session.createQuery(query);
        List<ChatRoom> results = q.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    public List<ChatRoom> getChatRoomsByKhoaId(Long khoaId) {
        Session session = factory.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<ChatRoom> query = builder.createQuery(ChatRoom.class);
        Root<ChatRoom> root = query.from(ChatRoom.class);

        Predicate predicate = builder.equal(root.get("khoa").get("id"), khoaId);
        query.where(predicate);

        Query<ChatRoom> q = session.createQuery(query);
        return q.getResultList();
    }

}

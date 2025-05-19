package com.desmin.repositories.impl;

import com.desmin.pojo.Message;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class MessageRepositoryImpl implements com.desmin.repositories.MessageRepository {

    @Autowired
    private LocalSessionFactoryBean factory;

    @Override
    public Message save(Message message) {
        Session session = factory.getObject().getCurrentSession();
        if (message.getId() == null) {
            session.persist(message);
        } else {
            session.merge(message);
        }
        session.flush();
        return message;
    }

    @Override
    public List<Message> findByRoomId(Long roomId) {
        Session session = factory.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Message> query = builder.createQuery(Message.class);
        Root<Message> root = query.from(Message.class);

        query.where(builder.equal(root.get("room").get("id"), roomId));
        query.orderBy(builder.asc(root.get("timestamp")));

        Query<Message> q = session.createQuery(query);
        return q.getResultList();
    }
}
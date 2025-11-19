package ru.redcarpet.dao;

import org.hibernate.HibernateException;

import ru.redcarpet.entity.User;
import ru.redcarpet.util.HibernateUtil;

public class UserDao {

    public void create(User user) {
        try (var session = HibernateUtil.openSession()) {
            session.beginTransaction();

            session.persist(user);

            session.getTransaction().commit();
        } catch (HibernateException e) {
            e.printStackTrace();
        }
    }
}

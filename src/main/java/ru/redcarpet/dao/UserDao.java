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

    public User findById(Long id) {
        User user = null;
        try (var session = HibernateUtil.openSession()) {
            session.beginTransaction();

            user = session.get(User.class, id);

            session.getTransaction().commit();
        } catch (HibernateException e) {
            e.printStackTrace();
        }
        return user;
    }

    public User delete(Long id) {
        User user = null;
        try (var session = HibernateUtil.openSession()) {
            session.beginTransaction();

            user = session.get(User.class, id);
            session.remove(user);

            session.getTransaction().commit();
        } catch (HibernateException e) {
            e.printStackTrace();
        }
        return user;
    }

    public void update(User user) {
        try (var session = HibernateUtil.openSession()) {
            session.beginTransaction();

            session.merge(user);

            session.getTransaction().commit();
        } catch (HibernateException e) {
            e.printStackTrace();
        }
    }
}

package ru.redcarpet;

import java.time.LocalDate;

import org.hibernate.HibernateException;
import org.hibernate.cfg.Configuration;

import ru.redcarpet.entity.User;

public class HibernateRunner {
    public static void main(String[] args) {
        Configuration configuration = new Configuration();
        configuration.configure();

        try (
            var sessionFactory = configuration.buildSessionFactory();
            var session = sessionFactory.openSession();
        ) {
            session.beginTransaction();

            session.persist(new User(null, "Ivan", "Ivan@mail.ru", LocalDate.of(2001, 04, 11), LocalDate.now()));

            session.getTransaction().commit();
        } catch (HibernateException e) {
            e.printStackTrace();
        }
    }
}
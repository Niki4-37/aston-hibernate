package ru.redcarpet.util;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import ru.redcarpet.exception.AppException;

import ru.redcarpet.entity.User;

public final class HibernateUtil {

    private static SessionFactory factory;

    static {
        Configuration configuration = new Configuration();
        configuration.configure();

        try {
            factory = configuration.buildSessionFactory();
        } catch (HibernateException e) {
            throw new AppException(e.getMessage(), e);
        } 
    }

    private HibernateUtil() {}

    public static Session openSession() throws HibernateException {
        return factory.openSession();
    }

    public static synchronized void rebuildSessionFactory() {
        if (factory != null) {
            factory.close();
        }
        Configuration cfg = new Configuration();

        cfg.setProperty("hibernate.connection.url", System.getProperty("connection.url"));
        cfg.setProperty("hibernate.connection.username", System.getProperty("connection.username"));
        cfg.setProperty("hibernate.connection.password", System.getProperty("connection.password"));
        cfg.setProperty("hibernate.hbm2ddl.auto", System.getProperty("hbm2ddl.auto"));
        cfg.setProperty("hibernate.show_sql", System.getProperty("show_sql"));

        cfg.addAnnotatedClass(User.class);

        factory = cfg.buildSessionFactory();
    }

}

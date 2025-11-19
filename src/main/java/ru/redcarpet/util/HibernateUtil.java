package ru.redcarpet.util;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public final class HibernateUtil {

    private static SessionFactory factory;

    static {
        Configuration configuration = new Configuration();
        configuration.configure();

        try {
            factory = configuration.buildSessionFactory();
        } catch (HibernateException e) {
            e.printStackTrace();
        } 
    }

    private HibernateUtil() {}

    public static Session openSession() throws HibernateException {
        return factory.openSession();
    }

}

package ru.redcarpet.util;

import java.util.function.Consumer;
import java.util.function.Function;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import ru.redcarpet.exception.AppException;

public class TransactionHelper {

    public void executeInTransaction(Consumer<Session> action) {
        Transaction transaction = null;
        try (var session = HibernateUtil.openSession()) {
            transaction = session.getTransaction();
            transaction.begin();

            action.accept(session);

            session.getTransaction().commit();

        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new AppException(e.getMessage(), e);
        }
    }

    public<T> T executeInTransaction(Function<Session, T> action) {
        Transaction transaction = null;
        try (var session = HibernateUtil.openSession()) {
            transaction = session.getTransaction();
            transaction.begin();

            var result = action.apply(session);

            session.getTransaction().commit();
            return result;
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new AppException(e.getMessage(), e);
        }
    }
}

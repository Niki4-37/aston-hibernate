package ru.redcarpet.util;

import java.util.function.Consumer;
import java.util.function.Function;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.redcarpet.exception.AppException;

public class TransactionHelper {

    private static final Logger LOG = LoggerFactory.getLogger(TransactionHelper.class);

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
            Throwable cause = e.getCause();
            if (cause instanceof java.sql.SQLException sqlExeption) {
                String sqlState = sqlExeption.getSQLState();
                int errorCode = sqlExeption.getErrorCode();
                LOG.error(
                        "PostgresSQL error: state = {}, error code = {}, message: {}",
                        sqlState,
                        errorCode,
                        sqlExeption.getMessage());
                throw new AppException("Database error: " + sqlExeption.getMessage(), sqlExeption);
            }

            throw new AppException(e.getMessage(), e);
        }
    }
}

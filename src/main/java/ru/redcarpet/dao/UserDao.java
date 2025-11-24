package ru.redcarpet.dao;

import org.hibernate.HibernateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.redcarpet.dto.UserDto;
import ru.redcarpet.entity.User;
import ru.redcarpet.exception.AppException;
import ru.redcarpet.mapper.UserMapper;
import ru.redcarpet.util.HibernateUtil;

public class UserDao {

    private final UserMapper MAPPER;
    private static final Logger LOG = LoggerFactory.getLogger(UserDao.class);

    public UserDao(UserMapper mapper) {
        this.MAPPER = mapper;
    }

    public UserDto create(UserDto userDto) {
        if (userDto.id() != null) {
            throw new AppException("ID should be null");
        }
        try (var session = HibernateUtil.openSession()) {
            session.beginTransaction();

            session.persist(MAPPER.toEntity(userDto));

            session.getTransaction().commit();

        } catch (HibernateException e) {
            throw new AppException(e.getMessage(), e);
        }
        return userDto;
    }

    public UserDto findById(Long id) {
        if (id <0) {
            throw new AppException("Negative ID");
        }
        User entity = null;

        try (var session = HibernateUtil.openSession()) {
            session.beginTransaction();

            entity = session.get(User.class, id);
            if (entity == null) {
                throw new AppException("Can't find user with such ID:" + id);
            }

            session.getTransaction().commit();
        } catch (HibernateException e) {
            throw new AppException(e.getMessage(), e);
        }
        return MAPPER.toDTO(entity);
    }

    public UserDto delete(Long id) {
        if (id <0) {
            throw new AppException("Negative ID");
        }
        User entity = null;
        try (var session = HibernateUtil.openSession()) {
            session.beginTransaction();

            entity = session.get(User.class, id);
            session.remove(entity);

            session.getTransaction().commit();
        } catch (HibernateException e) {
            throw new AppException(e.getMessage(), e);
        }
        LOG.info("Successfully deleted user with ID={}", id);
        return MAPPER.toDTO(entity);
    }

    public UserDto update(Long id, UserDto userDto) {
        if (id == null) {
            throw new AppException("ID should be not null");
        }
        User entity = null;
        try (var session = HibernateUtil.openSession()) {
            session.beginTransaction();

            entity = session.get(User.class, id);
            if (entity == null) {
                throw new AppException("Can't find user with such ID:" + id);
            }

            entity = MAPPER.toEntity(userDto);
            entity.setId(id);
            
            session.merge(entity);

            session.getTransaction().commit();
        } catch (HibernateException e) {
            throw new AppException(e.getMessage(), e);
        }
        LOG.info("Successfully updated user with ID={}", entity.getId());
        return MAPPER.toDTO(entity);
    }
}

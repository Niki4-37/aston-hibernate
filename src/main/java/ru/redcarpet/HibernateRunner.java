package ru.redcarpet;

import java.time.LocalDate;

import ru.redcarpet.dao.UserDao;
import ru.redcarpet.entity.User;

public class HibernateRunner {
    public static void main(String[] args) {
        
        var userDao = new UserDao();
        var newUser = new User(null, "Ivan", "Ivan@mail.ru", LocalDate.of(2001, 04, 11), LocalDate.now());
        userDao.create(newUser);

    }
}
package ru.redcarpet.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDate;

import org.hibernate.Session;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import ru.redcarpet.dto.UserDto;
import ru.redcarpet.util.HibernateUtil;

@Testcontainers
public class UserDaoTest {

    @Container
    public static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>(
        DockerImageName.parse("postgres:14.19-alpine3.21")
            .asCompatibleSubstituteFor("postgres"))
        .withDatabaseName("testdb")
        .withUsername("sa")
        .withPassword("sa");


    private static UserDao USER_DAO;

    @BeforeAll
    static void setUp() {
        System.setProperty("connection.url", POSTGRES.getJdbcUrl());
        System.setProperty("connection.username", POSTGRES.getUsername());
        System.setProperty("connection.password", POSTGRES.getPassword());
        System.setProperty("hbm2ddl.auto", "create-drop");
        System.setProperty("show_sql", "true");

        HibernateUtil.rebuildSessionFactory();

        USER_DAO = new UserDao();
    }

    @Test
    @DisplayName("create new user and check in DB")
    void createAndCheckInDB() {
        UserDto newUser = new UserDto(null, "Ben", "bigben@example.com", LocalDate.of(2000,10,10), LocalDate.now());
        USER_DAO.create(newUser);

        try(Session session = HibernateUtil.openSession()) {
            Long id = session.createQuery(
                "select u.id from User u where u.email = :email and u.name = :name", Long.class)
                .setParameter("email", newUser.email())
                .setParameter("name", newUser.name())
                .uniqueResult();
            assertNotNull(id);
        }
    }

    @Test
    @DisplayName("create and delete existing user")
    void deleteExistingUser() {
        UserDto newUser = new UserDto(null, "Jim Spear", "sharpspear@example.com", LocalDate.of(1980,01,20), LocalDate.now());
        USER_DAO.create(newUser);

        Long id;
        try(Session session = HibernateUtil.openSession()) {
            id = session.createQuery(
                "select u.id from User u where u.email = :email and u.name = :name", Long.class)
                .setParameter("email", newUser.email())
                .setParameter("name", newUser.name())
                .uniqueResult();
        }

        USER_DAO.delete(id);

        try(Session session = HibernateUtil.openSession()) {
            id = session.createQuery(
                "select u.id from User u where u.email = :email and u.name = :name", Long.class)
                .setParameter("email", newUser.email())
                .setParameter("name", newUser.name())
                .uniqueResult();
            assertNull(id);
        }
    }

    @Test
    @DisplayName("create new user and find by id")
    void testFindById() {
        UserDto newUser = new UserDto(null, "Bubble Boy", "bubbleboy@example.com", LocalDate.of(1985,05,05), LocalDate.now());
        USER_DAO.create(newUser);

        Long id;
        try(Session session = HibernateUtil.openSession()) {
            id = session.createQuery(
                "select u.id from User u where u.email = :email and u.name = :name", Long.class)
                .setParameter("email", newUser.email())
                .setParameter("name", newUser.name())
                .uniqueResult();
        }

        UserDto foundUser = USER_DAO.findById(id);

        assertNotNull(foundUser);
    }

    @Test
    @DisplayName("create new user and update data")
    void testUpdate() {
        UserDto newUser = new UserDto(null, "New Boy", "newboy@example.com", LocalDate.of(1985,05,05), LocalDate.now());
        USER_DAO.create(newUser);

        Long id;
        try(Session session = HibernateUtil.openSession()) {
            id = session.createQuery(
                "select u.id from User u where u.email = :email and u.name = :name and u.createdAt = :createdAt", Long.class)
                .setParameter("email", newUser.email())
                .setParameter("name", newUser.name())
                .setParameter("createdAt", newUser.createdAt())
                .uniqueResult();
        }

        UserDto updatedUser = new UserDto(null, "Yellow Boy", "yellowboy@example.com", LocalDate.of(1990,06,10), newUser.createdAt());
        UserDto userFromDB = USER_DAO.update(id, updatedUser);
        assertEquals(updatedUser, userFromDB);
    }
}

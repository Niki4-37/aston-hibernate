package ru.redcarpet.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import ru.redcarpet.dto.UserDto;
import ru.redcarpet.exception.AppException;
import ru.redcarpet.mapper.UserMapper;
import ru.redcarpet.util.HibernateUtil;

@Testcontainers
public class UserDaoTest {

    private final UserDto TEST_USER = new UserDto(
        null, 
        "Ben", 
        "bigben@example.com", 
        LocalDate.of(2000,10,10),
        LocalDate.of(2025,12,4));

    @Container
    static PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>(
        DockerImageName.parse("postgres:14.19-alpine3.21")
            .asCompatibleSubstituteFor("postgres"))
        .withDatabaseName("testdb")
        .withUsername("sa")
        .withPassword("sa");


    static UserDao userDao;
    static UserMapper mapper;

    @BeforeAll
    static void setUp() {
        System.setProperty("connection.url", POSTGRES.getJdbcUrl());
        System.setProperty("connection.username", POSTGRES.getUsername());
        System.setProperty("connection.password", POSTGRES.getPassword());
        System.setProperty("hbm2ddl.auto", "create-drop");
        System.setProperty("show_sql", "true");

        HibernateUtil.rebuildSessionFactory();

        userDao = new UserDao();
        mapper = new UserMapper();
    }

    @BeforeEach
    void prepareDB() {
        saveUserInDB(TEST_USER);
    }

    @AfterEach
    void clearDB() {
        try (Session session = HibernateUtil.openSession()) {
            session.beginTransaction();
            session.createNativeQuery("TRUNCATE TABLE public.users RESTART IDENTITY CASCADE", Void.class).executeUpdate();
            session.getTransaction().commit();
        } catch (HibernateException e) {
            System.out.println(e.getMessage());
        }
    }

    private void saveUserInDB(UserDto user) {
        try (Session session = HibernateUtil.openSession()) {
            session.beginTransaction();
            session.persist(mapper.toEntity(user));
            session.getTransaction().commit();
        } catch (HibernateException e) {
            System.out.println(e.getMessage());
        }  
    }

    private Long getIdByEmailAndNameAndBirhtDate(UserDto u) {
        try (Session s = HibernateUtil.openSession()) {
            return s.createQuery(
                    "select id from User where email = :email and name = :name and birthDate = :birthDate",
                    Long.class)
                    .setParameter("email", u.email())
                    .setParameter("name", u.name())
                    .setParameter("birthDate", u.birthDate())
                    .uniqueResult();
        }
    }

    @Test
    @DisplayName("create new user - success")
    void createAndCheckInDBSuccess() {
        UserDto newUser = new UserDto(
            null, 
            "Jim Spear", 
            "sharpspear@example.com", 
            LocalDate.of(1980,01,20), 
            LocalDate.of(2025,11,30));
        userDao.create(newUser);
        Long id = getIdByEmailAndNameAndBirhtDate(newUser);
        assertNotNull(id);
    }

    @Test
    @DisplayName("create new user with null throw exception")
    void createUserWithNoDataThrowAppException() {

        AppException ex = assertThrows(AppException.class,
                () -> userDao.create(null));

        assertEquals("No new data", ex.getMessage());
    }

    @Test
    @DisplayName("delete existing user - success")
    void deleteExistingUserSuccess() {

        Long id = getIdByEmailAndNameAndBirhtDate(TEST_USER);

        userDao.delete(id);

        id = getIdByEmailAndNameAndBirhtDate(TEST_USER);
        assertNull(id);
    }

    @Test
    @DisplayName("delete user with wrong id throw exception")
    void deleteUserNotFoundThrowsAppException() {
        Long notExistingId = 42L;

        AppException ex = assertThrows(AppException.class,
                () -> userDao.delete(notExistingId));

        assertEquals("Can't find user with such ID:" + notExistingId, ex.getMessage());
    }

    @Test
    @DisplayName("find by id - success")
    void findByIdSuccess() {

        Long id = getIdByEmailAndNameAndBirhtDate(TEST_USER);

        UserDto foundUser = userDao.findById(id);

        assertNotNull(foundUser);
    }

    @Test
    @DisplayName("find not existing user throw exception")
    void findUserWithWrongIdThrowAppException() {
        Long notExistingId = 42L;

        AppException ex = assertThrows(AppException.class,
                () -> userDao.findById(notExistingId));

        assertEquals("Can't find user with such ID:" + notExistingId, ex.getMessage());
    }

    @Test
    @DisplayName("update data - success")
    void updateSuccess() {

        Long id = getIdByEmailAndNameAndBirhtDate(TEST_USER);

        UserDto updatedUser = new UserDto(
            null, 
            "Yellow Boy", 
            "yellowboy@example.com", 
            LocalDate.of(1990,06,10), 
            TEST_USER.createdAt());
        UserDto userFromDB = userDao.update(id, updatedUser);
        assertEquals(updatedUser, userFromDB);
    }

    @Test
    @DisplayName("update not existing user throw exception")
    void updateUserWithWrongIdThrowAppException() {
        Long notExistingId = 42L;

        AppException ex = assertThrows(AppException.class,
                () -> userDao.update(notExistingId, TEST_USER));

        assertEquals("Can't find user with such ID:" + notExistingId, ex.getMessage());
    }

    @Test
    @DisplayName("update with null user throw exception")
    void updateUserWithNoDataThrowAppException() {
        Long id = getIdByEmailAndNameAndBirhtDate(TEST_USER);

        AppException ex = assertThrows(AppException.class,
                () -> userDao.update(id, null));

        assertEquals("No new data", ex.getMessage());
    }
}

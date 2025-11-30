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
import ru.redcarpet.mapper.UserMapper;
import ru.redcarpet.util.HibernateUtil;

@Testcontainers
public class UserDaoTest {

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

    private UserDto createTestUserAndSaveInDB(String name, String email, LocalDate birthDate) {
        UserDto dto = new UserDto(
            null,
                name,
                email,
                birthDate,
                LocalDate.now());
        try (Session session = HibernateUtil.openSession()) {
            session.beginTransaction();
            session.persist(mapper.toEntity(dto));
            session.getTransaction().commit();
        }
        return dto;
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
    @DisplayName("create new user and check in DB")
    void createAndCheckInDBSuccess() {
        UserDto newUser = createTestUserAndSaveInDB("Ben", "bigben@example.com", LocalDate.of(2000,10,10));

        Long id = getIdByEmailAndNameAndBirhtDate(newUser);
        assertNotNull(id);
    }

    @Test
    @DisplayName("create and delete existing user")
    void deleteExistingUserSuccess() {
        UserDto newUser = createTestUserAndSaveInDB("Jim Spear", "sharpspear@example.com", LocalDate.of(1980,01,20));

        Long id = getIdByEmailAndNameAndBirhtDate(newUser);

        userDao.delete(id);

        id = getIdByEmailAndNameAndBirhtDate(newUser);
        assertNull(id);
    }

    @Test
    @DisplayName("create new user and find by id")
    void findByIdSuccess() {
        UserDto newUser = createTestUserAndSaveInDB("Bubble Boy", "bubbleboy@example.com", LocalDate.of(1985,05,05));

        Long id = getIdByEmailAndNameAndBirhtDate(newUser);

        UserDto foundUser = userDao.findById(id);

        assertNotNull(foundUser);
    }

    @Test
    @DisplayName("create new user and update data")
    void updateSuccess() {
        UserDto newUser = createTestUserAndSaveInDB("New Boy", "newboy@example.com", LocalDate.of(1985,05,05));

        Long id = getIdByEmailAndNameAndBirhtDate(newUser);

        UserDto updatedUser = new UserDto(null, "Yellow Boy", "yellowboy@example.com", LocalDate.of(1990,06,10), newUser.createdAt());
        UserDto userFromDB = userDao.update(id, updatedUser);
        assertEquals(updatedUser, userFromDB);
    }
}

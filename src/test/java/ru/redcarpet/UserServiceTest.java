package ru.redcarpet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import ru.redcarpet.dao.UserRepository;
import ru.redcarpet.dto.UserDto;
import ru.redcarpet.entity.User;
import ru.redcarpet.exception.AppException;

@Testcontainers
@SpringBootTest(properties = "console.runner.enabled=false")
public class UserServiceTest {

    @Container
    static PostgreSQLContainer postgres = new PostgreSQLContainer(
        DockerImageName.parse("postgres:14.19-alpine3.21")
            .asCompatibleSubstituteFor("postgres"))
        .withDatabaseName("testdb")
        .withUsername("sa")
        .withPassword("sa");

    @Autowired
    UserRepository repo;

    @Autowired
    UserService service;

    final User testUser = new User(
        null, 
        "Ben", 
        "bigben@example.com", 
        LocalDate.of(2000,10,10),
        LocalDate.of(2025,12,4));

    Long testUserId;

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @BeforeEach
    void prepareDb() {
        User user = repo.save(testUser);
        testUserId = user.getId();
    }

    @Test
    @Transactional
    void testCreateUser() {
        UserDto newUser = new UserDto(
        null, 
        "Sara", 
        "lalala@example.com", 
        LocalDate.of(2000,01,20),
        LocalDate.of(2025,12,9));
        var savedUser = service.createUser(newUser);
        assertNotNull(savedUser.id());
    }

    @Test
    @Transactional
    void testDeleteUser() {
        UserDto deletedUse = service.deleteUser(testUserId);
        assertNotNull(deletedUse);
    }

    @Test
    void testGetUserById() {
        UserDto foundUser = service.getUserById(testUserId);
        assertNotNull(foundUser);
    }

    @Test
    void testTryGetUserByWrongId() {
        Long wrongId = 999L;
        AppException exception = assertThrows(
            AppException.class, 
            () -> service.getUserById(wrongId)
        );

        assertEquals("Can't find user with such ID:" + wrongId, exception.getMessage());
    }

    @Test
    @Transactional
    void testUpdateUser() {
        UserDto userToUpdate = new UserDto(
        testUserId, 
        "Sara", 
        "lalala@example.com", 
        LocalDate.of(2000,01,20),
        testUser.getCreatedAt());
        UserDto updatedUser = service.updateUser(testUserId, userToUpdate);
        assertEquals(userToUpdate, updatedUser);
    }
}

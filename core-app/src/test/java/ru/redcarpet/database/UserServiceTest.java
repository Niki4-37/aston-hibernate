package ru.redcarpet.database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;

import com.zaxxer.hikari.HikariDataSource;

import jakarta.persistence.EntityNotFoundException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import ru.redcarpet.database.dto.UserDto;
import ru.redcarpet.database.entity.User;
import ru.redcarpet.database.repository.UserRepository;
import ru.redcarpet.kafka.dto.KafkaUser;

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

    @MockitoBean
    KafkaTemplate<String, KafkaUser> kafkaTemplate;

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

    @AfterEach
    void clearDb() {
        if (repo.count() > 0) {
            repo.deleteAll();
        }
    }

    @AfterAll
    static void tearDown(@Autowired(required = false)HikariDataSource hikariDataSource) {
        closeHikariPool(hikariDataSource);
    }

    private static void closeHikariPool(HikariDataSource hikariDataSource) {
        if (hikariDataSource != null && !hikariDataSource.isClosed()) {
            try {
                hikariDataSource.close();
            } catch (Exception e) {
                System.err.println("Error closing Hikari pool: " + e.getMessage());
            }
        }
    }

    @Test
    void testCreateUser() {
        UserDto newUser = new UserDto(
        null, 
        "Sara", 
        "lalala@example.com", 
        LocalDate.of(2000,01,20),
        LocalDate.of(2025,12,9));
        
        when(kafkaTemplate.send(anyString(), anyString(), any(KafkaUser.class)))
            .thenReturn(CompletableFuture.completedFuture(null));
        
            var savedUser = service.createUser(newUser);
        assertNotNull(savedUser.id());
    }

    @Test
    void createUserWithSameEmailThrowException() {
        UserDto newUser= new UserDto(
            null,
            "Jim",
            testUser.getEmail(),
            LocalDate.of(2000, 01, 01),
            LocalDate.of(2025,12,9));
        var exception = assertThrows(
            DataIntegrityViolationException.class, 
            () -> service.createUser(newUser)
        );

        assertTrue(exception.getMessage().contains("duplicate key value violates unique constraint \"users_email_key\""));
    }

    @Test
    void testDeleteUser() {
        when(kafkaTemplate.send(anyString(), anyString(), any(KafkaUser.class)))
            .thenReturn(CompletableFuture.completedFuture(null));
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
        var exception = assertThrows(
            EntityNotFoundException.class, 
            () -> service.getUserById(wrongId)
        );

        assertEquals("Can't find user with such ID:" + wrongId, exception.getMessage());
    }

    @Test
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

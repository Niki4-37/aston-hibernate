package ru.redcarpet;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import ru.redcarpet.dao.UserDao;
import ru.redcarpet.dto.UserDto;
import ru.redcarpet.exception.AppException;
import ru.redcarpet.util.ConsoleHandler;

public class DispatcherTest {
    
    private final LocalDate CREATED_AT = LocalDate.of(2025, 11, 30);
    Dispatcher dispatcher;

    UserDao mockUserDao;
    MockedStatic<ConsoleHandler> consoleMock;
    

    @BeforeEach
    void setUp() {
        mockUserDao = mock(UserDao.class);
        dispatcher = new Dispatcher();

        java.lang.reflect.Field daoField;
        try {
            daoField = Dispatcher.class.getDeclaredField("userDao");
            daoField.setAccessible(true);
            daoField.set(dispatcher, mockUserDao);
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }

        consoleMock = mockStatic(ConsoleHandler.class);
    }

    @AfterEach
    void tearDown() {
        consoleMock.close();
    }

    @Test
    @DisplayName("test findById method with valid id - success")
    void testFindByIdSuccess() {
        // Arrange
        String idInput = "42";
        UserDto expectedUser = new UserDto(
            42L, 
            "Alice", 
            "alice@mail.com",
            LocalDate.of(1990, 5, 15), 
            CREATED_AT);

        consoleMock.when(ConsoleHandler::read).thenReturn(idInput);
        when(mockUserDao.findById(42L)).thenReturn(expectedUser);

        // Act
        UserDto actual = dispatcher.findByid();

        // Assert
        assertSame(expectedUser, actual);
        consoleMock.verify(() -> ConsoleHandler.write("find user with id: "));
    }

    @Test
    @DisplayName("test findById method with invalid id - success")
    void testFindByIdWithException() throws Exception {
        String idInput = "invalid";
        consoleMock.when(ConsoleHandler::read).thenReturn(idInput);
        when(mockUserDao.findById(anyLong()))
                .thenThrow(new AppException("Bad ID"));

        UserDto actual = dispatcher.findByid();
        assertNull(actual);
        consoleMock.verify(() -> ConsoleHandler.write(
                "find user with id: "));
        consoleMock.verify(() -> ConsoleHandler.write(
                "Bad ID try again"));
    }

    @Test
    @DisplayName("test create method with manual input - success")
    void testCreateWithMAnualInputSuccess() throws Exception {
        String idInput = "Jack jack@example.com 10-10-2000";
        UserDto createdUser = new UserDto(
            null, 
            "Jack", 
            "jack@example.com", 
            LocalDate.of(2000, 10, 10), 
            CREATED_AT);
        consoleMock.when(ConsoleHandler::read).thenReturn(idInput);        

        UserDto result = dispatcher.create();

        assertEquals(createdUser.name(), result.name());
        assertEquals(createdUser.email(), result.email());
        verify(mockUserDao).create(eq(result));
    }

    @Test
    @DisplayName("test create method with exception validation failed")
    void testCreateException() {
        String input = "ggg badmail invalid-date";
        consoleMock.when(ConsoleHandler::read).thenReturn(input);

        UserDto result = dispatcher.create();

        assertNull(result);
        consoleMock.verify(() -> ConsoleHandler.write(
                "to create new user write: \"name\" \"e-mail\" \"birht date\" in format \"DD-MM-YYYY\" "));
        consoleMock.verify(() -> ConsoleHandler.write("Validation failed"));
    }

    @Test
    @DisplayName("test update method - success")
    void testUpdateSuccess() {
        String idInput = "10";
        UserDto existing = new UserDto(
            10L, 
            "Old", 
            "old@mail.com",
            LocalDate.of(1995, 3, 12),
            CREATED_AT);
        String newDescription = "New new@mail.com 02-02-2000";
        UserDto updated = new UserDto(
            10L, 
            "New", 
            "new@mail.com",
            LocalDate.of(2000, 2, 2),
            CREATED_AT);

        consoleMock.when(ConsoleHandler::read)
                   .thenReturn(idInput)
                   .thenReturn(newDescription);

        when(mockUserDao.findById(10L)).thenReturn(existing);

        UserDto result = dispatcher.update();

        assertEquals(updated.name(), result.name());
        verify(mockUserDao).update(eq(10L), eq(result));
    }

    @Test
    @DisplayName("test update with exception no such user found")
    void testUpdateException() {
        String idInput = "999";
        consoleMock.when(ConsoleHandler::read).thenReturn(idInput);

        when(mockUserDao.findById(999L))
                .thenThrow(new AppException("Can't find user with such ID:999 try again"));

        try {
            dispatcher.update();
        } catch (AppException e) {
            assertTrue(e.getMessage().contains("Can't find user with such ID:999 try again"));
        }
    }

    @Test
    @DisplayName("test delete method - success")
    void testDeleteSuccess() {
        String idInput = "7";
        UserDto deleted = new UserDto(
            7L, 
            "Del", 
            "del@mail.com",
            LocalDate.of(1970, 7, 7),
            CREATED_AT);

        consoleMock.when(ConsoleHandler::read).thenReturn(idInput);
        when(mockUserDao.delete(7L)).thenReturn(deleted);

        UserDto result = dispatcher.delete();

        assertSame(deleted, result);
    }

    @Test
    @DisplayName("test delete with exception bad id")
    void testDeleteException() {
        String idInput = "invalid";
        consoleMock.when(ConsoleHandler::read).thenReturn(idInput);

        when(mockUserDao.delete(anyLong()))
                .thenThrow(new AppException("Bad ID"));

        try {
            dispatcher.delete();
        } catch (AppException e) {
            assertTrue(e.getMessage().contains("Bad ID"));
        }
    }
}

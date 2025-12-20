package ru.redcarpet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.time.LocalDate;

import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import ru.redcarpet.database.UserController;
import ru.redcarpet.database.dto.UserDto;
import ru.redcarpet.exception.AppException;
import ru.redcarpet.util.ConsoleHandler;

@SpringBootTest
public class DispatcherTest {
    
    private final LocalDate CREATED_AT = LocalDate.of(2025, 11, 30);
    
    @MockitoBean
    UserController controller;
    @Autowired
    Dispatcher dispatcher;

    MockedStatic<ConsoleHandler> consoleMock;
    

    @BeforeEach
    void setUp() {
        consoleMock = mockStatic(ConsoleHandler.class);
    }

    @AfterEach
    void tearDown() {
        consoleMock.close();
    }

    @Test
    @DisplayName("test invalid console command")
    void testInvalidConsoleCommand() {
        String methodName = "test";
        String message = dispatcher.methods.getOrDefault(methodName, () -> "There is no such method: " + methodName + " try again").get();
        assertNotNull(message);
        assertTrue(message.contains(message), "Mesage should contains invalid command");
        assertEquals("There is no such method: " + methodName + " try again", message);
    }

    @Test
    @DisplayName("test findById method with valid id - success")
    void testFindByIdSuccess() {
        String idInput = "42";
        UserDto expectedUser = new UserDto(
            42L, 
            "Alice", 
            "alice@mail.com",
            LocalDate.of(1990, 5, 15), 
            CREATED_AT);

        consoleMock.when(ConsoleHandler::read).thenReturn(idInput);
        when(controller.getUserById(42L)).thenReturn(expectedUser.toString());

        String message = dispatcher.findByid();

        assertEquals("Found " + expectedUser.toString(), message);
        consoleMock.verify(() -> ConsoleHandler.write("find user with id: "));
    }

    @Test
    @DisplayName("test findById method with invalid id - success")
    void testFindByIdWithInvalidId() throws Exception {
        String idInput = "invalid";
        consoleMock.when(ConsoleHandler::read).thenReturn(idInput);
         
        dispatcher.findByid();
 
        consoleMock.verify(() -> ConsoleHandler.write(
                "find user with id: "));
        consoleMock.verify(() -> ConsoleHandler.write(
                "Bad ID"));
    }

    @Test
    @DisplayName("test create method with manual input - success")
    void testCreateWithMAnualInputSuccess() throws Exception {
        String idInput = "Jack jack@example.com 10-10-2000";
        UserDto createdUser = new UserDto(
            14L, 
            "Jack", 
            "jack@example.com", 
            LocalDate.of(2000, 10, 10), 
            CREATED_AT);
        consoleMock.when(ConsoleHandler::read).thenReturn(idInput);        
        when(controller.createUser(any(UserDto.class))).thenReturn(createdUser.toString());
        String message = dispatcher.create();
        assertEquals("Successfully created " + createdUser.toString(), message);
        consoleMock.verify(() -> ConsoleHandler.write(
                "to create new user write: \"name\" \"e-mail\" \"birht date\" in format \"DD-MM-YYYY\" "));
    }

    @Test
    @DisplayName("test create method with exception validation failed")
    void testCreateException() {
        String input = "ggg badmail invalid-date";
        consoleMock.when(ConsoleHandler::read).thenReturn(input);

        dispatcher.create();

        consoleMock.verify(() -> ConsoleHandler.write(
                "to create new user write: \"name\" \"e-mail\" \"birht date\" in format \"DD-MM-YYYY\" "));
        consoleMock.verify(() -> ConsoleHandler.write("Wrong e-mail format"));
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

        when(controller.getUserById(10L)).thenReturn(existing.toString());
        when(controller.updateUser(eq(10L), any(UserDto.class))).thenReturn(updated.toString());

        String message = dispatcher.update();

        assertEquals("Successfully updated " + updated.toString(), message);
    }

    @Test
    @DisplayName("test update with exception no such user found")
    void testUpdateException() {
        String idInput = "999";
        consoleMock.when(ConsoleHandler::read).thenReturn(idInput);

        when(controller.getUserById(999L))
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
        when(controller.deleteUser(7L)).thenReturn(deleted.toString());

        String message = dispatcher.delete();

        assertEquals("Successfully deleted " + deleted.toString(), message);
    }

    @Test
    @DisplayName("test delete with exception bad id")
    void testDeleteException() {
        String idInput = "invalid";
        consoleMock.when(ConsoleHandler::read).thenReturn(idInput);

        when(controller.deleteUser(anyLong()))
                .thenThrow(new AppException("Bad ID"));

        dispatcher.delete();
        consoleMock.verify(() -> ConsoleHandler.write(
                "Bad ID"));
    }
}

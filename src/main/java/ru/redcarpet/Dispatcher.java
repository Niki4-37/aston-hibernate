package ru.redcarpet;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import jakarta.annotation.PreDestroy;
import ru.redcarpet.database.UserController;
import ru.redcarpet.database.dto.UserDto;
import ru.redcarpet.exception.AppException;
import ru.redcarpet.util.ConsoleHandler;
import ru.redcarpet.util.UserFromStringConverter;

@Component
@ConditionalOnProperty(name = "console.runner.enabled", havingValue = "true")
public class Dispatcher implements CommandLineRunner {

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final UserController controller;
    
    public Dispatcher(UserController controller) {
        this.controller = controller;
    }

    Map<String, Supplier<String>> methods = new HashMap<>();

    {
        methods.put("1", () -> findByid());
        methods.put("2", () -> create());
        methods.put("3", () -> update());
        methods.put("4", () -> delete());
    }

    @Override
    public void run(String... args) {
        executor.submit(this::consoleLoop);
    }

    private void consoleLoop(){
        while (true) {
            ConsoleHandler.write("""
                type number of method to use it: 
                1 - find
                2 - create
                3 - update
                4 - delete 
                or type \"exit\" to leave program""");
            String methodName = ConsoleHandler.read().toLowerCase().trim();
            
            String message = methods.getOrDefault(methodName, () -> "There is no such method: " + methodName + " try again").get();
            ConsoleHandler.write(message);
        }
    }

    @PreDestroy
    public void shutdown() {
        executor.shutdownNow();
    }

    String findByid() {
        ConsoleHandler.write("find user with id: ");
        
        Long id = -1L;
        try {
            id = Long.valueOf(ConsoleHandler.read());
        } catch (NumberFormatException e) {
            ConsoleHandler.write("Bad ID");
        }

        try {
            return "Found " + controller.getUserById(id);
        } catch (AppException e) {
            ConsoleHandler.write(e.getMessage() + " try again");
        }
        return "";
    }

    String create() {
        ConsoleHandler.write("to create new user write: \"name\" \"e-mail\" \"birht date\" in format \"DD-MM-YYYY\" ");
        String userDescription = ConsoleHandler.read();
        try {
            var userDto = UserFromStringConverter.convert(userDescription);
            return "Successfully created " + controller.createUser(userDto);
        } catch (AppException e) {
            ConsoleHandler.write(e.getMessage());
        }
        return "";
    }

    String update() {
        ConsoleHandler.write("update user with id: ");
        Long id = -1L;
        try {
            id = Long.valueOf(ConsoleHandler.read());
        } catch (NumberFormatException e) {
            ConsoleHandler.write("Bad ID");
        }
        try {
            String message = controller.getUserById(id);
            ConsoleHandler.write(message + "to update user write: \"name\" \"e-mail\" \"birht date\" in format \"DD-MM-YYYY\" ");
            String userDescription = ConsoleHandler.read();
            UserDto updatedUserDto = UserFromStringConverter.convert(userDescription);
            return "Successfully updated " + controller.updateUser(id, updatedUserDto);
        } catch (AppException e) {
            ConsoleHandler.write(e.getMessage() + " try again");
        }
        return "";
    }

    String delete() {
        ConsoleHandler.write("delete user with id: ");
        Long id = -1L;
        try {
            id = Long.valueOf(ConsoleHandler.read());
        } catch (NumberFormatException e) {
            ConsoleHandler.write("Bad ID");
        }
        try {
            return "Successfully deleted " + controller.deleteUser(id);
        } catch (AppException e) {
            ConsoleHandler.write(e.getMessage() + " try again");
        }
        return "";
    }
}

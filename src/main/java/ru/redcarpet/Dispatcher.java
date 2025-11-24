package ru.redcarpet;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import ru.redcarpet.dao.UserDao;
import ru.redcarpet.dto.UserDto;
import ru.redcarpet.exception.AppException;
import ru.redcarpet.mapper.UserMapper;
import ru.redcarpet.util.ConsoleHandler;

public class Dispatcher {

    private final UserDao userDao;
    private Map<String, Supplier<UserDto>> methods = new HashMap<>();

    {
        methods.put("find",() -> findByid());
        methods.put("create", () -> create());
        methods.put("update", () -> update());
        methods.put("delete", ()->delete());
    }

    public Dispatcher(UserMapper mapper) {
        this.userDao = new UserDao(mapper);
    }

    public void run() {
        while (true) {
            ConsoleHandler.write("choose method \"find\" \"create\" \"update\" \"delete\" or type \"exit\" to leave program");
            String methodName = ConsoleHandler.read().toLowerCase().trim();
            var user = methods.get(methodName).get();
            if (user != null) {
                ConsoleHandler.write(user.toString());
            }
        }
    }

    private UserDto findByid() {
        ConsoleHandler.write("find user with id: ");
        String id = ConsoleHandler.read();
        UserDto user = null;
        try {
            user = userDao.findById(Long.valueOf(id));
        } catch (AppException e) {
            ConsoleHandler.write(e.getMessage() + " try again");
        }
        return user;
    }

    private UserDto create() {
        ConsoleHandler.write("to create new user write: \"name\" \"e-mail\" \"birht date\" in format \"DD-MM-YYYY\" ");
        String userDescription = ConsoleHandler.read();
        String[] values = userDescription.split(" ");
        if (values.length < 3) {
            ConsoleHandler.write("wrong data");
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate birthDate = LocalDate.parse(values[2], formatter);
        var userDto = new UserDto(null, values[0], values[1], birthDate, LocalDate.now());
        try {
            userDao.create(userDto);
        } catch (AppException e) {
            ConsoleHandler.write(e.getMessage());
        }
        return userDto;
    }

    private UserDto update() {
        ConsoleHandler.write("update user with id: ");
        String id = ConsoleHandler.read();
        UserDto user = null;
        try {
            user = userDao.findById(Long.valueOf(id));
        } catch (AppException e) {
            ConsoleHandler.write(e.getMessage() + " try again");
        }
        ConsoleHandler.write(user.toString());
        ConsoleHandler.write("to update user write: \"name\" \"e-mail\" \"birht date\" in format \"DD-MM-YYYY\" ");
        String userDescription = ConsoleHandler.read();
        String[] values = userDescription.split(" ");
        if (values.length < 3) {
            ConsoleHandler.write("wrong data");
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate birthDate = LocalDate.parse(values[2], formatter);
        var updatedUserDto = new UserDto(user.id(), values[0], values[1], birthDate, user.createdAt());
        try {
            userDao.update(updatedUserDto);
        } catch (AppException e) {
            ConsoleHandler.write(e.getMessage());
        }
        return updatedUserDto;        
    }

    private UserDto delete() {
        ConsoleHandler.write("delete user with id: ");
        String id = ConsoleHandler.read();
        UserDto user = null;
        try {
            user = userDao.delete(Long.valueOf(id));
        } catch (AppException e) {
            ConsoleHandler.write(e.getMessage() + " try again");
        }
        return user;
    }
}

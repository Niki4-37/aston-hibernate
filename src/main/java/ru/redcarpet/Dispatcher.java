package ru.redcarpet;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import ru.redcarpet.dao.UserDao;
import ru.redcarpet.dto.UserDto;
import ru.redcarpet.exception.AppException;
import ru.redcarpet.util.ConsoleHandler;
import ru.redcarpet.util.UserFromStringConverter;

public class Dispatcher {

    private final UserDao userDao;
    private Map<String, Supplier<UserDto>> methods = new HashMap<>();

    {
        methods.put("find",() -> findByid());
        methods.put("create", () -> create());
        methods.put("update", () -> update());
        methods.put("delete", ()->delete());
    }

    public Dispatcher() {
        this.userDao = new UserDao();
    }

    public void run() {
        while (true) {
            ConsoleHandler.write("choose method \"find\" \"create\" \"update\" \"delete\" or type \"exit\" to leave program");
            String methodName = ConsoleHandler.read().toLowerCase().trim();
            UserDto user = null;
            
            if (methods.containsKey(methodName)) {
                user = methods.get(methodName).get();
            } else {
                ConsoleHandler.write("There is no such method: " + methodName + " try again");
            }

            if (user != null) {
                ConsoleHandler.write(user.toString());
            }
        }
    }

    UserDto findByid() {
        ConsoleHandler.write("find user with id: ");
        
        Long id = -1L;
        try {
            id = Long.valueOf(ConsoleHandler.read());
        } catch (NumberFormatException e) {
            ConsoleHandler.write("Bad ID");
        }

        UserDto user = null;
        try {
            user = userDao.findById(id);
        } catch (AppException e) {
            ConsoleHandler.write(e.getMessage() + " try again");
        }
        return user;
    }

    UserDto create() {
        ConsoleHandler.write("to create new user write: \"name\" \"e-mail\" \"birht date\" in format \"DD-MM-YYYY\" ");
        String userDescription = ConsoleHandler.read();
        UserDto userDto = null;
        try {
            userDto = UserFromStringConverter.convert(userDescription);
            userDao.create(userDto);
        } catch (AppException e) {
            ConsoleHandler.write(e.getMessage());
        }
        return userDto;
    }

    UserDto update() {
        ConsoleHandler.write("update user with id: ");
        Long id = -1L;
        try {
            id = Long.valueOf(ConsoleHandler.read());
        } catch (NumberFormatException e) {
            ConsoleHandler.write("Bad ID");
        }
        UserDto user = null;
        try {
            user = userDao.findById(id);
            ConsoleHandler.write(String.format(
                """
                Found user %s
                to update user write: 
                \"name\" \"e-mail\" \"birht date\" in format \"DD-MM-YYYY\"
                """
                , user.toString()));
            String userDescription = ConsoleHandler.read();
            UserDto updatedUserDto = UserFromStringConverter.convert(userDescription);
            userDao.update(user.id(), updatedUserDto);
            user = updatedUserDto;
        } catch (AppException e) {
            ConsoleHandler.write(e.getMessage() + " try again");
        }
        return user;
    }

    UserDto delete() {
        ConsoleHandler.write("delete user with id: ");
        Long id = -1L;
        try {
            id = Long.valueOf(ConsoleHandler.read());
        } catch (NumberFormatException e) {
            ConsoleHandler.write("Bad ID");
        }
        UserDto user = null;
        try {
            user = userDao.delete(id);
        } catch (AppException e) {
            ConsoleHandler.write(e.getMessage() + " try again");
        }
        return user;
    }
}

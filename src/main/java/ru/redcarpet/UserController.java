package ru.redcarpet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import ru.redcarpet.dto.UserDto;

@Controller
public class UserController {

    private final UserService service;
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    public UserController(UserService service) {
        this.service = service;
    }

    public String getUserById(Long id) {
        return service.getUserById(id).toString();
    }

    public String createUser(UserDto userDto) {
        return service.createUser(userDto).toString();
    }

    public String updateUser(Long id, UserDto updatedUserDto) {
        return service.updateUser(id, updatedUserDto).toString();
    }

    public String deleteUser(Long id) {
        return service.deleteUser(id).toString();
    }
}

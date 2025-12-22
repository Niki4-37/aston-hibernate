package ru.redcarpet.database;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.validation.Valid;
import ru.redcarpet.database.dto.UserDto;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(
        @PathVariable("id") Long id
    ) {
        return ResponseEntity.ok(service.getUserById(id));
    }

    @PostMapping
    public ResponseEntity<UserDto> createUser(
        @RequestBody @Valid UserDto userDto
    ) {
        return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(service.createUser(userDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(
        @PathVariable("id")Long id,
        @RequestBody @Valid UserDto updatedUserDto
    ) {
        var updated = service.updateUser(id, updatedUserDto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/id")
    public ResponseEntity<UserDto> deleteUser(
        @PathVariable("id") Long id
    ) {
        return ResponseEntity.ok(service.deleteUser(id));
    }
}

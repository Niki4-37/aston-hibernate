package ru.redcarpet.database;

import org.springframework.hateoas.EntityModel;
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

import org.springframework.http.MediaType;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import ru.redcarpet.database.dto.UserDto;
import ru.redcarpet.exception.ErrorDto;

@Controller
@RequestMapping("/users")
@Tag(name = "User controller", description = "CRUD operations for user")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by id",
        responses = {
            @ApiResponse(
                responseCode = "200", 
                description = "User found",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            ),
            @ApiResponse(
                responseCode = "404", 
                description = "User not found",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                   schema = @Schema(implementation = ErrorDto.class))
    )})
    public ResponseEntity<EntityModel<UserDto>> getUserById(
        @PathVariable("id") Long id
    ) {
        return ResponseEntity.ok(service.getUserById(id).toEntityModel());
    }

    @PostMapping
    @Operation(summary = "Create new user",
        responses = {
            @ApiResponse(
                responseCode = "201", 
                description = "User created",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            ),
            @ApiResponse(
                responseCode = "400", 
                description = "Bad request, validation fail, for more info check error message",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                   schema = @Schema(implementation = ErrorDto.class))
    )})
    public ResponseEntity<UserDto> createUser(
        @RequestBody @Valid UserDto userDto
    ) {
        return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(service.createUser(userDto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user",
        responses = {
            @ApiResponse(
                responseCode = "200", 
                description = "User updated",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            ),
            @ApiResponse(
                responseCode = "400", 
                description = "Bad request, validation fail, for more info check error message",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                   schema = @Schema(implementation = ErrorDto.class))
    )})
    public ResponseEntity<EntityModel<UserDto>> updateUser(
        @PathVariable("id")Long id,
        @RequestBody @Valid UserDto updatedUserDto
    ) {
        var updated = service.updateUser(id, updatedUserDto);
        return ResponseEntity.ok(updated.toEntityModel());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user",
        responses = {
            @ApiResponse(
                responseCode = "200", 
                description = "User deleted",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            ),
            @ApiResponse(
                responseCode = "404", 
                description = "User not found", 
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                   schema = @Schema(implementation = ErrorDto.class))
    )})
    public ResponseEntity<UserDto> deleteUser(
        @PathVariable("id") Long id
    ) {
        return ResponseEntity.ok(service.deleteUser(id));
    }
}

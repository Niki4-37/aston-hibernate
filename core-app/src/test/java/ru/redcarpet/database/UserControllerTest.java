package ru.redcarpet.database;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import jakarta.persistence.EntityNotFoundException;
import ru.redcarpet.database.dto.UserDto;

@WebMvcTest(UserController.class)
public class UserControllerTest {
    
    @Autowired private MockMvc mockMvc;
    @MockitoBean private UserService service;

    UserDto testDto;
    ObjectMapper objectMapper;

    {
        testDto = new UserDto(
            10L,
            "Tester",
            "tester@mail.com",
            LocalDate.of(1990, 10, 10),
            LocalDate.of(2025, 12, 24)
        );
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }
    
    
    @Test
    @DisplayName("POST /users - success")
    void testCreateUser() throws JsonProcessingException, Exception {
        var requestDto = new UserDto(
            null, 
            testDto.name(), 
            testDto.email(), 
            testDto.birthDate(), 
            null);
        when(service.createUser(Mockito.any(UserDto.class))).thenReturn(testDto);
        
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.createdAt", is("2025-12-24")))
                .andExpect(jsonPath("$.id", is(10)));
    }

    @Test
    @DisplayName("POST /users - validation error")
    void createUserValidationError() throws Exception {
        
        UserDto requestDto = new UserDto(
            null, 
            "Captain", 
            "", 
            testDto.birthDate(),
        null);

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage",
                        containsString("empty email field")));
    }

    @Test
    @DisplayName("DELETE /users/10 - success")
    void testDeleteUser() throws Exception {
        when(service.deleteUser(10L)).thenReturn(testDto);

        mockMvc.perform(delete("/users/{id}", 10L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(10)));
    }

    @Test
    @DisplayName("GET /users/10 - success")
    void testGetUserById() throws Exception {
        when(service.getUserById(10L)).thenReturn(testDto);

        mockMvc.perform(get("/users/{id}", 10L)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(10)))
                .andExpect(jsonPath("$.name", is("Tester")))
                .andExpect(jsonPath("$.email", is("tester@mail.com")))
                .andExpect(jsonPath("$.birthDate", is("1990-10-10")));
    }

    @Test
    @DisplayName("GET /users/{id} - user not found")
    void getUserById_NotFound() throws Exception {
        when(service.getUserById(anyLong())).thenThrow(new EntityNotFoundException("Can't find user with such ID:999"));

        mockMvc.perform(get("/users/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /users/10 - success")
    void testUpdateUser() throws Exception {
        UserDto request = new UserDto(
            null,
            "Captain",
            "CaptainMorgan@example.com", 
            testDto.birthDate(), 
            null);
        UserDto updated = new UserDto(
            testDto.id(), 
            request.name(),
            request.email(),
            testDto.birthDate(),
            testDto.createdAt());

        when(service.updateUser(Mockito.eq(10L), Mockito.any(UserDto.class))).thenReturn(updated);

        mockMvc.perform(put("/users/{id}", 10L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Captain")))
                .andExpect(jsonPath("$.email", is("CaptainMorgan@example.com")));
    }
}

package com.example.online_shop.user.controller;

import com.example.online_shop.user.dto.UserDto;
import com.example.online_shop.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class,
        excludeAutoConfiguration = {
                org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
                org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration.class
        })
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    // Mock security filter bean to avoid loading full security stack
    @MockBean
    private com.example.online_shop.configuration.jwt.JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    @DisplayName("GET /api/v1/users/{username} returns user DTO")
    void getUser() throws Exception {
        UserDto dto = new UserDto();
        dto.setUsername("jane");
        Mockito.when(userService.getUserByUsername("jane")).thenReturn(dto);

        mockMvc.perform(get("/api/v1/users/{username}", "jane"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("jane")));
    }
}

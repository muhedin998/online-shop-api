package com.example.online_shop.address.controller;

import com.example.online_shop.address.dto.AddressDto;
import com.example.online_shop.address.service.AddressService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AddressController.class,
        excludeAutoConfiguration = {
                org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
                org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration.class
        })
@AutoConfigureMockMvc(addFilters = false)
class AddressControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AddressService addressService;

    // Mock security filter bean to avoid loading full security stack
    @MockBean
    private com.example.online_shop.configuration.jwt.JwtAuthenticationFilter jwtAuthenticationFilter;

    private AddressDto address(Long id) {
        AddressDto dto = new AddressDto();
        dto.setId(id);
        dto.setFullName("John Doe");
        dto.setAddressLine1("123 Main");
        dto.setCity("City");
        dto.setPostalCode("12345");
        dto.setCountryCode("US");
        return dto;
    }

    @Test
    @DisplayName("POST /api/v1/addresses/{userId} creates address")
    void create() throws Exception {
        Mockito.when(addressService.createAddress(eq(5L), any(AddressDto.class))).thenReturn(address(1L));

        AddressDto req = address(null);

        mockMvc.perform(post("/api/v1/addresses/{userId}", 5L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()));
    }

    @Test
    @DisplayName("GET /api/v1/addresses/{userId} lists addresses")
    void list() throws Exception {
        Mockito.when(addressService.listAddresses(9L)).thenReturn(List.of(address(1L), address(2L)));

        mockMvc.perform(get("/api/v1/addresses/{userId}", 9L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }
}

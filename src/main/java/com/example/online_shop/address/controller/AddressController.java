package com.example.online_shop.address.controller;

import com.example.online_shop.address.dto.AddressDto;
import com.example.online_shop.address.service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/addresses")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @PostMapping("/{userId}")
    @Operation(summary = "Create a new address for the user")
    public ResponseEntity<AddressDto> create(@PathVariable Long userId,
                                             @Valid @RequestBody AddressDto request) {
        AddressDto created = addressService.createAddress(userId, request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{userId}")
    @Operation(summary = "List addresses for the user")
    public ResponseEntity<List<AddressDto>> list(@PathVariable Long userId) {
        return ResponseEntity.ok(addressService.listAddresses(userId));
    }
}


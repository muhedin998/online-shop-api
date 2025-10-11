package com.example.online_shop.address.service;

import com.example.online_shop.address.dto.AddressDto;
import com.example.online_shop.address.mapper.AddressMapper;
import com.example.online_shop.address.model.Address;
import com.example.online_shop.address.repository.AddressRepository;
import com.example.online_shop.address.service.impl.AddressServiceImpl;
import com.example.online_shop.shared.exception.domain.UserNotFoundException;
import com.example.online_shop.user.model.User;
import com.example.online_shop.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddressServiceImplTest {

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AddressMapper addressMapper;

    @InjectMocks
    private AddressServiceImpl addressService;

    private User testUser;
    private Address testAddress;
    private AddressDto testAddressDto;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        testAddress = new Address();
        testAddress.setId(1L);
        testAddress.setUser(testUser);
        testAddress.setFullName("John Doe");
        testAddress.setAddressLine1("123 Main St");
        testAddress.setCity("Test City");
        testAddress.setPostalCode("12345");
        testAddress.setCountryCode("US");

        testAddressDto = new AddressDto();
        testAddressDto.setId(1L);
        testAddressDto.setFullName("John Doe");
        testAddressDto.setAddressLine1("123 Main St");
        testAddressDto.setCity("Test City");
        testAddressDto.setPostalCode("12345");
        testAddressDto.setCountryCode("us");
    }

    @Test
    void createAddress_ValidUser_Success() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(addressMapper.toEntity(testAddressDto)).thenReturn(testAddress);
        when(addressRepository.save(any(Address.class))).thenReturn(testAddress);
        when(addressMapper.toDto(testAddress)).thenReturn(testAddressDto);

        // Act
        AddressDto result = addressService.createAddress(1L, testAddressDto);

        // Assert
        assertNotNull(result);
        verify(userRepository).findById(1L);
        verify(addressMapper).toEntity(testAddressDto);
        verify(addressRepository).save(any(Address.class));
        verify(addressMapper).toDto(testAddress);
    }

    @Test
    void createAddress_NonExistingUser_ThrowsException() {
        // Arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class,
                () -> addressService.createAddress(999L, testAddressDto));
        verify(addressRepository, never()).save(any());
    }

    @Test
    void createAddress_ConvertsCountryCodeToUpperCase() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(addressMapper.toEntity(testAddressDto)).thenReturn(testAddress);
        when(addressRepository.save(any(Address.class))).thenAnswer(invocation -> {
            Address saved = invocation.getArgument(0);
            assertEquals("US", saved.getCountryCode());
            return saved;
        });
        when(addressMapper.toDto(testAddress)).thenReturn(testAddressDto);

        // Act
        AddressDto result = addressService.createAddress(1L, testAddressDto);

        // Assert
        assertNotNull(result);
        verify(addressRepository).save(any(Address.class));
    }

    @Test
    void listAddresses_ReturnsUserAddresses() {
        // Arrange
        List<Address> addresses = Arrays.asList(testAddress);
        when(addressRepository.findByUserId(1L)).thenReturn(addresses);
        when(addressMapper.toDto(testAddress)).thenReturn(testAddressDto);

        // Act
        List<AddressDto> result = addressService.listAddresses(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(addressRepository).findByUserId(1L);
        verify(addressMapper).toDto(testAddress);
    }

    @Test
    void listAddresses_EmptyList_ReturnsEmptyList() {
        // Arrange
        when(addressRepository.findByUserId(1L)).thenReturn(Arrays.asList());

        // Act
        List<AddressDto> result = addressService.listAddresses(1L);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.size());
        verify(addressRepository).findByUserId(1L);
    }
}

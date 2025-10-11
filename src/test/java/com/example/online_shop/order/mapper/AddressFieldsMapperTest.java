package com.example.online_shop.order.mapper;

import com.example.online_shop.address.dto.AddressDto;
import com.example.online_shop.address.model.Address;
import com.example.online_shop.order.model.AddressFields;
import com.example.online_shop.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AddressFieldsMapperTest {

    private AddressFieldsMapper mapper;
    private Address testAddress;
    private AddressDto testAddressDto;

    @BeforeEach
    void setUp() {
        mapper = new AddressFieldsMapper();

        User testUser = new User();
        testUser.setId(1L);

        testAddress = new Address();
        testAddress.setId(1L);
        testAddress.setUser(testUser);
        testAddress.setFullName("John Doe");
        testAddress.setAddressLine1("123 Main St");
        testAddress.setAddressLine2("Apt 4B");
        testAddress.setCity("Test City");
        testAddress.setState("TestState");
        testAddress.setPostalCode("12345");
        testAddress.setCountryCode("us");
        testAddress.setPhone("+1234567890");

        testAddressDto = new AddressDto();
        testAddressDto.setFullName("Jane Smith");
        testAddressDto.setAddressLine1("456 Oak Ave");
        testAddressDto.setAddressLine2("Suite 200");
        testAddressDto.setCity("Another City");
        testAddressDto.setState("AnotherState");
        testAddressDto.setPostalCode("67890");
        testAddressDto.setCountryCode("ca");
        testAddressDto.setPhone("+0987654321");
    }

    @Test
    void toAddressFields_FromAddress_Success() {
        // Act
        AddressFields result = mapper.toAddressFields(testAddress);

        // Assert
        assertNotNull(result);
        assertEquals("John Doe", result.getFullName());
        assertEquals("123 Main St", result.getAddressLine1());
        assertEquals("Apt 4B", result.getAddressLine2());
        assertEquals("Test City", result.getCity());
        assertEquals("TestState", result.getState());
        assertEquals("12345", result.getPostalCode());
        assertEquals("US", result.getCountryCode()); // Should be uppercase
        assertEquals("+1234567890", result.getPhone());
    }

    @Test
    void toAddressFields_FromAddressDto_Success() {
        // Act
        AddressFields result = mapper.toAddressFields(testAddressDto);

        // Assert
        assertNotNull(result);
        assertEquals("Jane Smith", result.getFullName());
        assertEquals("456 Oak Ave", result.getAddressLine1());
        assertEquals("Suite 200", result.getAddressLine2());
        assertEquals("Another City", result.getCity());
        assertEquals("AnotherState", result.getState());
        assertEquals("67890", result.getPostalCode());
        assertEquals("CA", result.getCountryCode()); // Should be uppercase
        assertEquals("+0987654321", result.getPhone());
    }

    @Test
    void toAddressFields_ConvertsCountryCodeToUpperCase() {
        // Arrange
        testAddress.setCountryCode("gb");

        // Act
        AddressFields result = mapper.toAddressFields(testAddress);

        // Assert
        assertEquals("GB", result.getCountryCode());
    }

    @Test
    void toAddressFields_FromDto_ConvertsCountryCodeToUpperCase() {
        // Arrange
        testAddressDto.setCountryCode("fr");

        // Act
        AddressFields result = mapper.toAddressFields(testAddressDto);

        // Assert
        assertEquals("FR", result.getCountryCode());
    }
}

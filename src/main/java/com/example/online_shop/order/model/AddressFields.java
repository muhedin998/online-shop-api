package com.example.online_shop.order.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class AddressFields {

    @Column(name = "shipping_full_name")
    private String fullName;

    @Column(name = "shipping_address_line1")
    private String addressLine1;

    @Column(name = "shipping_address_line2")
    private String addressLine2;

    @Column(name = "shipping_city")
    private String city;

    @Column(name = "shipping_state")
    private String state;

    @Column(name = "shipping_postal_code")
    private String postalCode;

    @Column(name = "shipping_country_code", length = 2)
    private String countryCode;

    @Column(name = "shipping_phone")
    private String phone;
}

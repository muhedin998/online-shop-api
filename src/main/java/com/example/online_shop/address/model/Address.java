package com.example.online_shop.address.model;

import com.example.online_shop.user.model.User;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "addresses")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String addressLine1;

    private String addressLine2;

    @Column(nullable = false)
    private String city;

    private String state;

    @Column(nullable = false)
    private String postalCode;

    @Column(nullable = false, length = 2)
    private String countryCode;

    private String phone;

    private String label;

    @Column(nullable = false)
    private Boolean isDefaultShipping = false;

    @Column(nullable = false)
    private Boolean isDefaultBilling = false;

    @Column(nullable = false)
    private Boolean archived = false;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();
}


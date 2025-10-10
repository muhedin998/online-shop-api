package com.example.online_shop.shared.service;

public interface EmailService {

    void sendPasswordResetEmail(String to, String token);
}

package com.ecommerce.user_service.service;

public interface EmailService {
    void sendVerificationEmail(String to, String token);
    void sendPasswordResetEmail(String to, String token);
    void sendWelcomeEmail(String to, String name);
}
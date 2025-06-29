package com.ecommerce.user_service.service.impl;

import com.ecommerce.user_service.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.email.from}")
    private String fromEmail;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Override
    public void sendVerificationEmail(String to, String token) {
        log.info("Sending verification email to: {}", to);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject("Verify Your Email");
        message.setText("Please click the link below to verify your email:\n\n" +
                frontendUrl + "/verify-email?token=" + token + "\n\n" +
                "This link will expire in 24 hours.");

        mailSender.send(message);
        log.info("Verification email sent successfully");
    }

    @Override
    public void sendPasswordResetEmail(String to, String token) {
        log.info("Sending password reset email to: {}", to);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject("Password Reset Request");
        message.setText("Please click the link below to reset your password:\n\n" +
                frontendUrl + "/reset-password?token=" + token + "\n\n" +
                "This link will expire in 2 hours. If you did not request this, please ignore this email.");

        mailSender.send(message);
        log.info("Password reset email sent successfully");
    }

    @Override
    public void sendWelcomeEmail(String to, String name) {
        log.info("Sending welcome email to: {}", to);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject("Welcome to E-Commerce Platform!");
        message.setText("Hello " + name + ",\n\n" +
                "Welcome to our E-Commerce platform! Your account has been successfully created.\n\n" +
                "Start shopping now at: " + frontendUrl + "\n\n" +
                "Best regards,\nThe E-Commerce Team");

        mailSender.send(message);
        log.info("Welcome email sent successfully");
    }
}
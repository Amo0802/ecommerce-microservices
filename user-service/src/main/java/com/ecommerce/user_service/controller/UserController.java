package com.ecommerce.user_service.controller;

import com.ecommerce.user_service.dto.*;
import com.ecommerce.user_service.service.UserService;
import io.micrometer.core.annotation.Timed;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    @Timed(value = "user.register", description = "Time taken to register user")
    public ResponseEntity<UserDTO> register(@Valid @RequestBody RegisterRequest request) {
        log.info("REST request to register user: {}", request.getUsername());
        UserDTO user = userService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @PostMapping("/login")
    @Timed(value = "user.login", description = "Time taken to login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("REST request to login: {}", request.getUsernameOrEmail());
        LoginResponse response = userService.login(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    @Timed(value = "user.profile", description = "Time taken to get user profile")
    public ResponseEntity<UserDTO> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        log.debug("REST request to get current user profile");
        UserDTO user = userService.getUserByUsername(userDetails.getUsername());
        return ResponseEntity.ok(user);
    }

    @PutMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    @Timed(value = "user.update", description = "Time taken to update user")
    public ResponseEntity<UserDTO> updateProfile(@AuthenticationPrincipal UserDetails userDetails,
                                                 @Valid @RequestBody UpdateUserRequest request) {
        log.info("REST request to update user profile");
        UserDTO user = userService.getUserByUsername(userDetails.getUsername());
        UserDTO updatedUser = userService.updateUser(user.getId(), request);
        return ResponseEntity.ok(updatedUser);
    }

    @PostMapping("/change-password")
    @PreAuthorize("isAuthenticated()")
    @Timed(value = "user.changePassword", description = "Time taken to change password")
    public ResponseEntity<Void> changePassword(@AuthenticationPrincipal UserDetails userDetails,
                                               @Valid @RequestBody ChangePasswordRequest request) {
        log.info("REST request to change password");
        UserDTO user = userService.getUserByUsername(userDetails.getUsername());
        userService.changePassword(user.getId(), request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/forgot-password")
    @Timed(value = "user.forgotPassword", description = "Time taken to process forgot password")
    public ResponseEntity<Void> forgotPassword(@RequestParam String email) {
        log.info("REST request for password reset: {}", email);
        userService.requestPasswordReset(email);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/reset-password")
    @Timed(value = "user.resetPassword", description = "Time taken to reset password")
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        log.info("REST request to reset password");
        userService.resetPassword(request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/verify-email")
    @Timed(value = "user.verifyEmail", description = "Time taken to verify email")
    public ResponseEntity<Void> verifyEmail(@RequestParam String token) {
        log.info("REST request to verify email");
        userService.verifyEmail(token);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/resend-verification")
    @Timed(value = "user.resendVerification", description = "Time taken to resend verification")
    public ResponseEntity<Void> resendVerification(@RequestParam String email) {
        log.info("REST request to resend verification email: {}", email);
        userService.resendVerificationEmail(email);
        return ResponseEntity.noContent().build();
    }
}
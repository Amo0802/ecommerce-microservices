package com.ecommerce.user_service.service;

import com.ecommerce.user_service.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    UserDTO registerUser(RegisterRequest request);
    LoginResponse login(LoginRequest request);
    UserDTO getUserById(Long id);
    UserDTO getUserByUsername(String username);
    UserDTO updateUser(Long id, UpdateUserRequest request);
    void changePassword(Long userId, ChangePasswordRequest request);
    void requestPasswordReset(String email);
    void resetPassword(ResetPasswordRequest request);
    void verifyEmail(String token);
    void resendVerificationEmail(String email);
    Page<UserDTO> getAllUsers(Pageable pageable);
    void deleteUser(Long id);
    void lockUser(Long id);
    void unlockUser(Long id);
}
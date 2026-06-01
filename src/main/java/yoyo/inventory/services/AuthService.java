package yoyo.inventory.services;

import jakarta.servlet.http.HttpServletRequest;
import yoyo.inventory.dto.request.LoginRequest;
import yoyo.inventory.dto.request.RegisterRequest;
import yoyo.inventory.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request, HttpServletRequest httpRequest);
    AuthResponse login(LoginRequest request, HttpServletRequest httpRequest);
    AuthResponse refresh(String refreshToken, HttpServletRequest httpRequest);
    void logout(String refreshToken);
    void logoutAll(Long userId);
    AuthResponse socialLogin(yoyo.inventory.dto.request.SocialLoginRequest request, HttpServletRequest httpRequest);
    void forgotPassword(String emailOrUsername);
    void resetPassword(String token, String newPassword);
    void changePassword(String email, String currentPassword, String newPassword);
    void verifyEmail(String token);
    void resendVerificationEmail(String emailOrUsername);
}

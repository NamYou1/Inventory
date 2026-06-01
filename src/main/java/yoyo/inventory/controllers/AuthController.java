package yoyo.inventory.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import yoyo.inventory.dto.request.LoginRequest;
import yoyo.inventory.dto.request.LogoutRequest;
import yoyo.inventory.dto.request.RefreshTokenRequest;
import yoyo.inventory.dto.request.RegisterRequest;
import yoyo.inventory.dto.request.SocialLoginRequest;
import yoyo.inventory.dto.request.ResetPasswordRequest;
import yoyo.inventory.dto.request.ChangePasswordRequest;
import yoyo.inventory.dto.response.AuthResponse;
import yoyo.inventory.execption.ApiResponse;
import yoyo.inventory.services.AuthService;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ApiResponse<AuthResponse> register(@Valid @RequestBody RegisterRequest request, HttpServletRequest httpRequest) {
        return ApiResponse.<AuthResponse>builder()
                .success("true")
                .status(HttpStatus.CREATED)
                .message("Register success")
                .payload(authService.register(request, httpRequest))
                .timestamp(LocalDateTime.now())
                .build();
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        return ApiResponse.<AuthResponse>builder()
                .success("true")
                .status(HttpStatus.OK)
                .message("Login success")
                .payload(authService.login(request, httpRequest))
                .timestamp(LocalDateTime.now())
                .build();
    }

    @PostMapping("/refresh")
    public ApiResponse<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest request, HttpServletRequest httpRequest) {
        return ApiResponse.<AuthResponse>builder()
                .success("true")
                .status(HttpStatus.OK)
                .message("Token refreshed")
                .payload(authService.refresh(request.getRefreshToken(), httpRequest))
                .timestamp(LocalDateTime.now())
                .build();
    }

    @PostMapping("/logout")
    public ApiResponse<Map<String, String>> logout(@Valid @RequestBody LogoutRequest request) {
        authService.logout(request.getRefreshToken());
        return ApiResponse.<Map<String, String>>builder()
                .success("true")
                .status(HttpStatus.OK)
                .message("Logout success")
                .payload(Map.of("status", "ok"))
                .timestamp(LocalDateTime.now())
                .build();
    }

    @PostMapping("/logout-all/{userId}")
    public ApiResponse<Map<String, String>> logoutAll(@PathVariable Long userId) {
        authService.logoutAll(userId);
        return ApiResponse.<Map<String, String>>builder()
                .success("true")
                .status(HttpStatus.OK)
                .message("Logout all success")
                .payload(Map.of("status", "ok"))
                .timestamp(LocalDateTime.now())
                .build();
    }

    @PostMapping("/social-login")
    public ApiResponse<AuthResponse> socialLogin(@Valid @RequestBody SocialLoginRequest request, HttpServletRequest httpRequest) {
        return ApiResponse.<AuthResponse>builder()
                .success("true")
                .status(HttpStatus.OK)
                .message("Social login success")
                .payload(authService.socialLogin(request, httpRequest))
                .timestamp(LocalDateTime.now())
                .build();
    }

    @PostMapping("/forgot-password")
    public ApiResponse<Map<String, String>> forgotPassword(@RequestParam String emailOrUsername) {
        authService.forgotPassword(emailOrUsername);
        return ApiResponse.<Map<String, String>>builder()
                .success("true")
                .status(HttpStatus.OK)
                .message("Password reset token generated and sent")
                .payload(Map.of("status", "sent"))
                .timestamp(LocalDateTime.now())
                .build();
    }

    @PostMapping("/reset-password")
    public ApiResponse<Map<String, String>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request.getToken(), request.getNewPassword());
        return ApiResponse.<Map<String, String>>builder()
                .success("true")
                .status(HttpStatus.OK)
                .message("Password reset successfully")
                .payload(Map.of("status", "ok"))
                .timestamp(LocalDateTime.now())
                .build();
    }

    @PostMapping("/change-password")
    public ApiResponse<Map<String, String>> changePassword(Principal principal, @Valid @RequestBody ChangePasswordRequest request) {
        if (principal == null) {
            return ApiResponse.<Map<String, String>>builder()
                    .success("false")
                    .status(HttpStatus.UNAUTHORIZED)
                    .message("User is not authenticated")
                    .timestamp(LocalDateTime.now())
                    .build();
        }
        authService.changePassword(principal.getName(), request.getCurrentPassword(), request.getNewPassword());
        return ApiResponse.<Map<String, String>>builder()
                .success("true")
                .status(HttpStatus.OK)
                .message("Password changed successfully")
                .payload(Map.of("status", "ok"))
                .timestamp(LocalDateTime.now())
                .build();
    }

    @PostMapping("/verify-email")
    public ApiResponse<Map<String, String>> verifyEmail(@RequestParam String token) {
        authService.verifyEmail(token);
        return ApiResponse.<Map<String, String>>builder()
                .success("true")
                .status(HttpStatus.OK)
                .message("Email verified successfully")
                .payload(Map.of("status", "ok"))
                .timestamp(LocalDateTime.now())
                .build();
    }

    @PostMapping("/resend-verification")
    public ApiResponse<Map<String, String>> resendVerification(@RequestParam String emailOrUsername) {
        authService.resendVerificationEmail(emailOrUsername);
        return ApiResponse.<Map<String, String>>builder()
                .success("true")
                .status(HttpStatus.OK)
                .message("Verification email token generated and sent")
                .payload(Map.of("status", "sent"))
                .timestamp(LocalDateTime.now())
                .build();
    }
}

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
import yoyo.inventory.dto.response.AuthResponse;
import yoyo.inventory.execption.ApiResponse;
import yoyo.inventory.services.AuthService;

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
}

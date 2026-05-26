package yoyo.inventory.services.impl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import yoyo.inventory.dto.request.LoginRequest;
import yoyo.inventory.dto.request.RegisterRequest;
import yoyo.inventory.dto.response.AuthResponse;
import yoyo.inventory.entities.RefreshToken;
import yoyo.inventory.entities.User;
import yoyo.inventory.entities.Role;
import yoyo.inventory.entities.Permission;
import yoyo.inventory.execption.ResourceNotFoundException;
import yoyo.inventory.repository.RefreshTokenRepository;
import yoyo.inventory.repository.UserRepository;
import yoyo.inventory.services.AuthService;
import yoyo.inventory.config.JwtService;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Value("${app.jwt.refresh-expiration-seconds}")
    private long refreshExpirationSeconds;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request, HttpServletRequest httpRequest) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new ResourceNotFoundException("Username already exists");
        }
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ResourceNotFoundException("Email already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhone(request.getPhone());
        user.setIsActive(true);
        user.setIsLocked(false);
        user.setIsVerified(false);
        user.setFailedLoginAttempts(0);
        user = userRepository.save(user);

        String accessToken = jwtService.generateAccessToken(user);
        RefreshToken refreshToken = createRefreshToken(user, null, null, httpRequest);
        List<String> roles = user.getRoles().stream()
                .map(Role::getCode)
                .toList();
        List<String> permissions = user.getRoles().stream()
                .flatMap(r -> r.getPermissions().stream())
                .map(Permission::getCode)
                .distinct()
                .toList();

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .tokenType("Bearer")
                .expiresIn(jwtService.getAccessExpirationSeconds())
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(roles)
                .permissions(permissions)
                .storeId(user.getStore() != null ? user.getStore().getId() : null)
                .build();
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request, HttpServletRequest httpRequest) {
        User user = userRepository.findByUsernameOrEmail(request.getUsernameOrEmail(), request.getUsernameOrEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Invalid credentials"));

        if (Boolean.TRUE.equals(user.getIsLocked()) || Boolean.FALSE.equals(user.getIsActive()) || user.getDeletedAt() != null) {
            throw new ResourceNotFoundException("User is inactive or locked");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            user.setFailedLoginAttempts((user.getFailedLoginAttempts() == null ? 0 : user.getFailedLoginAttempts()) + 1);
            userRepository.save(user);
            throw new ResourceNotFoundException("Invalid credentials");
        }

        user.setFailedLoginAttempts(0);
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        String accessToken = jwtService.generateAccessToken(user);
        RefreshToken refreshToken = createRefreshToken(user, request.getDeviceId(), request.getDeviceName(), httpRequest);

        List<String> roles = user.getRoles().stream()
                .map(Role::getCode)
                .toList();
        List<String> permissions = user.getRoles().stream()
                .flatMap(r -> r.getPermissions().stream())
                .map(Permission::getCode)
                .distinct()
                .toList();

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .tokenType("Bearer")
                .expiresIn(jwtService.getAccessExpirationSeconds())
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(roles)
                .permissions(permissions)
                .storeId(user.getStore() != null ? user.getStore().getId() : null)
                .build();
    }

    @Override
    @Transactional
    public AuthResponse refresh(String refreshTokenValue, HttpServletRequest httpRequest) {
        RefreshToken existing = refreshTokenRepository.findByToken(refreshTokenValue)
                .orElseThrow(() -> new ResourceNotFoundException("Refresh token not found"));

        if (Boolean.TRUE.equals(existing.getIsRevoked()) || existing.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ResourceNotFoundException("Refresh token expired or revoked");
        }

        User user = existing.getUser();
        revoke(existing);
        RefreshToken rotated = createRefreshToken(user, existing.getDeviceId(), existing.getDeviceName(), httpRequest);
        String accessToken = jwtService.generateAccessToken(user);

        List<String> roles = user.getRoles().stream()
                .map(Role::getCode)
                .toList();
        List<String> permissions = user.getRoles().stream()
                .flatMap(r -> r.getPermissions().stream())
                .map(Permission::getCode)
                .distinct()
                .toList();

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(rotated.getToken())
                .tokenType("Bearer")
                .expiresIn(jwtService.getAccessExpirationSeconds())
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(roles)
                .permissions(permissions)
                .storeId(user.getStore() != null ? user.getStore().getId() : null)
                .build();
    }

    @Override
    @Transactional
    public void logout(String refreshTokenValue) {
        refreshTokenRepository.findByToken(refreshTokenValue).ifPresent(this::revoke);
    }

    @Override
    @Transactional
    public void logoutAll(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User" , userId));
        List<RefreshToken> tokens = refreshTokenRepository.findByUserAndIsRevokedFalse(user);
        tokens.forEach(this::revoke);
        refreshTokenRepository.saveAll(tokens);
    }

    private RefreshToken createRefreshToken(User user, String deviceId, String deviceName, HttpServletRequest request) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(generateSecureToken());
        refreshToken.setDeviceId(deviceId);
        refreshToken.setDeviceName(deviceName);
        refreshToken.setIpAddress(extractIp(request));
        refreshToken.setUserAgent(request != null ? request.getHeader("User-Agent") : null);
        refreshToken.setExpiresAt(LocalDateTime.now().plusSeconds(refreshExpirationSeconds));
        refreshToken.setIsRevoked(false);
        return refreshTokenRepository.save(refreshToken);
    }

    private void revoke(RefreshToken token) {
        token.setIsRevoked(true);
        token.setRevokedAt(LocalDateTime.now());
        refreshTokenRepository.save(token);
    }

    private String generateSecureToken() {
        byte[] bytes = new byte[64];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String extractIp(HttpServletRequest request) {
        if (request == null) return null;
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}

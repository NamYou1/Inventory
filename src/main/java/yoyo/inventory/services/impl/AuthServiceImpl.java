package yoyo.inventory.services.impl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import yoyo.inventory.dto.request.LoginRequest;
import yoyo.inventory.dto.request.RegisterRequest;
import yoyo.inventory.dto.request.SocialLoginRequest;
import yoyo.inventory.dto.response.AuthResponse;
import yoyo.inventory.entities.RefreshToken;
import yoyo.inventory.entities.User;
import yoyo.inventory.entities.Role;
import yoyo.inventory.entities.Permission;
import yoyo.inventory.entities.VerificationToken;
import yoyo.inventory.execption.ApiException;
import yoyo.inventory.execption.ResourceNotFoundException;
import yoyo.inventory.repository.RefreshTokenRepository;
import yoyo.inventory.repository.UserRepository;
import yoyo.inventory.repository.RoleRepository;
import yoyo.inventory.repository.VerificationTokenRepository;
import yoyo.inventory.services.AuthService;
import yoyo.inventory.config.JwtService;
import yoyo.inventory.utils.PasswordValidator;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final VerificationTokenRepository verificationTokenRepository;
    private final RoleRepository roleRepository;

    @Value("${app.jwt.refresh-expiration-seconds}")
    private long refreshExpirationSeconds;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request, HttpServletRequest httpRequest) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new ApiException(HttpStatus.CONFLICT, "Username already exists");
        }
        if (request.getEmail() != null) {
            if (!request.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid email format");
            }
            if (userRepository.findByEmail(request.getEmail()).isPresent()) {
                throw new ApiException(HttpStatus.CONFLICT, "Email already exists");
            }
        }
        if (request.getPhone() != null && !request.getPhone().isBlank()) {
            if (!request.getPhone().matches("^\\+?[0-9]{8,15}$")) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid phone number format");
            }
            if (userRepository.findByPhone(request.getPhone()).isPresent()) {
                throw new ApiException(HttpStatus.CONFLICT, "Phone number already exists");
            }
        }

        if (!PasswordValidator.isValid(request.getPassword())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, PasswordValidator.getRequirementsMessage());
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhone(request.getPhone());
        user.setProvider("LOCAL");
        user.setIsActive(true);
        user.setIsLocked(false);
        user.setIsVerified(false);
        user.setFailedLoginAttempts(0);
        // Assign default ROLE_STAFF
        roleRepository.findByCode("ROLE_STAFF").ifPresent(role -> user.getRoles().add(role));
        User savedUser = userRepository.save(user);
        // Auto-generate verification token for email verification
        generateAndSaveToken(savedUser, "EMAIL_VERIFICATION", 24);

        String accessToken = jwtService.generateAccessToken(savedUser);
        RefreshToken refreshToken = createRefreshToken(savedUser, null, null, httpRequest);
        List<String> roles = savedUser.getRoles().stream()
                .map(Role::getCode)
                .toList();
        List<String> permissions = savedUser.getRoles().stream()
                .flatMap(r -> r.getPermissions().stream())
                .map(Permission::getCode)
                .distinct()
                .toList();

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .tokenType("Bearer")
                .expiresIn(jwtService.getAccessExpirationSeconds())
                .userId(savedUser.getId())
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .roles(roles)
                .permissions(permissions)
                .storeId(savedUser.getStore() != null ? savedUser.getStore().getId() : null)
                .build();
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request, HttpServletRequest httpRequest) {
        User user = userRepository.findByUsernameOrEmailOrPhone(request.getUsernameOrEmail(), request.getUsernameOrEmail(), request.getUsernameOrEmail())
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        if (Boolean.TRUE.equals(user.getIsLocked()) || Boolean.FALSE.equals(user.getIsActive()) || user.getDeletedAt() != null) {
            throw new ApiException(HttpStatus.FORBIDDEN, "User is inactive or locked");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            user.setFailedLoginAttempts((user.getFailedLoginAttempts() == null ? 0 : user.getFailedLoginAttempts()) + 1);
            userRepository.save(user);
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
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
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Refresh token not found"));

        if (Boolean.TRUE.equals(existing.getIsRevoked()) || existing.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Refresh token expired or revoked");
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
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
        List<RefreshToken> tokens = refreshTokenRepository.findByUserAndIsRevokedFalse(user);
        tokens.forEach(this::revoke);
        refreshTokenRepository.saveAll(tokens);
    }

    @Override
    @Transactional
    public AuthResponse socialLogin(SocialLoginRequest request, HttpServletRequest httpRequest) {
        User user = userRepository.findByProviderAndProviderId(request.getProvider().toUpperCase(), request.getProviderId())
                .orElse(null);

        if (user == null && request.getEmail() != null) {
            // Link to existing local user with matching email
            user = userRepository.findByEmail(request.getEmail()).orElse(null);
            if (user != null) {
                user.setProvider(request.getProvider().toUpperCase());
                user.setProviderId(request.getProviderId());
                if (request.getAvatarUrl() != null && user.getAvatarUrl() == null) {
                    user.setAvatarUrl(request.getAvatarUrl());
                }
                user = userRepository.save(user);
            }
        }

        if (user == null) {
            // Register a new user automatically
            User newUser = new User();
            String uniqueUsername = request.getFirstName() + "_" + request.getLastName() + "_" + UUID.randomUUID().toString().substring(0, 8);
            newUser.setUsername(uniqueUsername);
            newUser.setEmail(request.getEmail());
            newUser.setFirstName(request.getFirstName());
            newUser.setLastName(request.getLastName());
            newUser.setAvatarUrl(request.getAvatarUrl());
            newUser.setPasswordHash(passwordEncoder.encode(UUID.randomUUID().toString())); // dummy password
            newUser.setProvider(request.getProvider().toUpperCase());
            newUser.setProviderId(request.getProviderId());
            newUser.setIsActive(true);
            newUser.setIsVerified(true); // social accounts are pre-verified
            newUser.setIsLocked(false);
            newUser.setFailedLoginAttempts(0);

            // Assign default role ROLE_STAFF
            roleRepository.findByCode("ROLE_STAFF").ifPresent(role -> newUser.getRoles().add(role));

            user = userRepository.save(newUser);
        }

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
    public void forgotPassword(String emailOrUsername) {
        User user = userRepository.findByUsernameOrEmail(emailOrUsername, emailOrUsername)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));

        verificationTokenRepository.deleteByUserAndType(user, "PASSWORD_RESET");

        VerificationToken token = generateAndSaveToken(user, "PASSWORD_RESET", 1);

        log.info("========================================");
        log.info("PASSWORD RESET LINK SIMULATION");
        log.info("For user: {}", user.getEmail());
        log.info("Reset Token: {}", token.getToken());
        log.info("Reset URL: http://localhost:8080/api/v1/auth/reset-password?token={}", token.getToken());
        log.info("========================================");
    }

    @Override
    @Transactional
    public void resetPassword(String tokenValue, String newPassword) {
        VerificationToken token = verificationTokenRepository.findByTokenAndType(tokenValue, "PASSWORD_RESET")
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Invalid or expired password reset token"));

        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            verificationTokenRepository.delete(token);
            throw new ApiException(HttpStatus.BAD_REQUEST, "Password reset token has expired");
        }

        if (!PasswordValidator.isValid(newPassword)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, PasswordValidator.getRequirementsMessage());
        }

        User user = token.getUser();
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setPasswordChangedAt(LocalDateTime.now());
        userRepository.save(user);

        verificationTokenRepository.delete(token);
    }

    @Override
    @Transactional
    public void changePassword(String email, String currentPassword, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));

        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Current password does not match");
        }

        if (!PasswordValidator.isValid(newPassword)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, PasswordValidator.getRequirementsMessage());
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setPasswordChangedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void verifyEmail(String tokenValue) {
        VerificationToken token = verificationTokenRepository.findByTokenAndType(tokenValue, "EMAIL_VERIFICATION")
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Invalid or expired email verification token"));

        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            verificationTokenRepository.delete(token);
            throw new ApiException(HttpStatus.BAD_REQUEST, "Verification token has expired");
        }

        User user = token.getUser();
        user.setIsVerified(true);
        user.setIsActive(true);
        userRepository.save(user);

        verificationTokenRepository.delete(token);
    }

    @Override
    @Transactional
    public void resendVerificationEmail(String emailOrUsername) {
        User user = userRepository.findByUsernameOrEmail(emailOrUsername, emailOrUsername)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));

        if (Boolean.TRUE.equals(user.getIsVerified())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "User is already verified");
        }

        verificationTokenRepository.deleteByUserAndType(user, "EMAIL_VERIFICATION");

        VerificationToken token = generateAndSaveToken(user, "EMAIL_VERIFICATION", 24);

        log.info("========================================");
        log.info("EMAIL VERIFICATION SIMULATION");
        log.info("For user: {}", user.getEmail());
        log.info("Verification Token: {}", token.getToken());
        log.info("Verification URL: http://localhost:8080/api/v1/auth/verify-email?token={}", token.getToken());
        log.info("========================================");
    }

    private VerificationToken generateAndSaveToken(User user, String type, int hoursToExpire) {
        VerificationToken token = new VerificationToken();
        token.setUser(user);
        token.setType(type);
        token.setToken(UUID.randomUUID().toString());
        token.setExpiresAt(LocalDateTime.now().plusHours(hoursToExpire));
        return verificationTokenRepository.save(token);
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

    private void    revoke(RefreshToken token) {
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

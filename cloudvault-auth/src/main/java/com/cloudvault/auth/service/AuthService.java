package com.cloudvault.auth.service;

import com.cloudvault.auth.dto.*;
import com.cloudvault.auth.model.User;
import com.cloudvault.auth.model.UserRole;
import com.cloudvault.auth.model.UserSession;
import com.cloudvault.auth.repository.UserRepository;
import com.cloudvault.auth.security.JwtTokenProvider;
import com.cloudvault.auth.security.UserPrincipal;
import com.cloudvault.common.exception.ConflictException;
import com.cloudvault.common.exception.ResourceNotFoundException;
import com.cloudvault.common.exception.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final SessionService sessionService;
    private final TwoFactorAuthService twoFactorAuthService;
    private final GoogleOAuthService googleOAuthService;
    private final JwtTokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, SessionService sessionService,
                       TwoFactorAuthService twoFactorAuthService, GoogleOAuthService googleOAuthService,
                       JwtTokenProvider tokenProvider, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.sessionService = sessionService;
        this.twoFactorAuthService = twoFactorAuthService;
        this.googleOAuthService = googleOAuthService;
        this.tokenProvider = tokenProvider;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserResponse register(UserRegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("An account with this email address already exists");
        }

        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .emailVerified(false)
                .twoFaEnabled(false)
                .role(UserRole.ROLE_USER)
                .build();

        User savedUser = userRepository.save(user);
        log.info("User registered successfully: {}", savedUser.getEmail());

        return mapToUserResponse(savedUser);
    }

    @Transactional
    public TokenResponse login(UserLoginRequest request, String ipAddress, String userAgent) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new UnauthorizedException("Invalid email or password");
        }

        UserPrincipal principal = UserPrincipal.create(user);

        if (user.isTwoFaEnabled()) {
            log.info("User {} login successful, requiring 2FA", user.getEmail());
            String tempAccessToken = tokenProvider.generateAccessToken(principal);
            return TokenResponse.builder()
                    .accessToken(tempAccessToken)
                    .twoFaRequired(true)
                    .build();
        }

        log.info("User {} logged in successfully (no 2FA)", user.getEmail());
        UserSession session = sessionService.createSession(user, request.getDeviceFingerprint(), ipAddress, userAgent);
        String accessToken = tokenProvider.generateAccessToken(principal);

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(session.getRefreshToken())
                .expiresIn(900)
                .twoFaRequired(false)
                .build();
    }

    @Transactional
    public TokenResponse verify2FaLogin(String email, String code, String deviceFingerprint, String ipAddress, String userAgent) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!user.isTwoFaEnabled()) {
            throw new UnauthorizedException("2FA is not enabled for this user");
        }

        boolean isValid = twoFactorAuthService.verifyCode(user.getTwoFaSecret(), code);
        if (!isValid) {
            throw new UnauthorizedException("Invalid 2FA verification code");
        }

        log.info("User {} verified 2FA successfully", user.getEmail());
        UserPrincipal principal = UserPrincipal.create(user);
        UserSession session = sessionService.createSession(user, deviceFingerprint, ipAddress, userAgent);
        String accessToken = tokenProvider.generateAccessToken(principal);

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(session.getRefreshToken())
                .expiresIn(900)
                .twoFaRequired(false)
                .build();
    }

    @Transactional
    public TwoFaSetupResponse enable2Fa(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.isTwoFaEnabled()) {
            throw new ConflictException("2FA is already enabled");
        }

        String secret = twoFactorAuthService.generateNewSecret();
        user.setTwoFaSecret(secret);
        userRepository.save(user);

        String qrCodeUrl = twoFactorAuthService.getQrCodeUrl(user.getEmail(), secret);
        return TwoFaSetupResponse.builder()
                .secret(secret)
                .qrCodeUrl(qrCodeUrl)
                .build();
    }

    @Transactional
    public void confirm2Fa(UUID userId, String code) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.isTwoFaEnabled()) {
            throw new ConflictException("2FA is already enabled");
        }

        if (user.getTwoFaSecret() == null) {
            throw new ConflictException("2FA setup not initialized");
        }

        boolean isValid = twoFactorAuthService.verifyCode(user.getTwoFaSecret(), code);
        if (!isValid) {
            throw new UnauthorizedException("Invalid verification code");
        }

        user.setTwoFaEnabled(true);
        userRepository.save(user);
        log.info("2FA enabled for user ID: {}", userId);
    }

    @Transactional
    public void disable2Fa(UUID userId, String code) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!user.isTwoFaEnabled()) {
            throw new ConflictException("2FA is not enabled");
        }

        boolean isValid = twoFactorAuthService.verifyCode(user.getTwoFaSecret(), code);
        if (!isValid) {
            throw new UnauthorizedException("Invalid verification code");
        }

        user.setTwoFaEnabled(false);
        user.setTwoFaSecret(null);
        userRepository.save(user);
        log.info("2FA disabled for user ID: {}", userId);
    }

    @Transactional
    public TokenResponse googleLogin(String idToken, String deviceFingerprint, String ipAddress, String userAgent) {
        GoogleOAuthService.GoogleUserPayload googleUser = googleOAuthService.verifyToken(idToken);

        User user = userRepository.findByEmail(googleUser.getEmail())
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .email(googleUser.getEmail())
                            .passwordHash(passwordEncoder.encode(UUID.randomUUID().toString()))
                            .firstName(googleUser.getFirstName())
                            .lastName(googleUser.getLastName())
                            .emailVerified(true)
                            .twoFaEnabled(false)
                            .role(UserRole.ROLE_USER)
                            .build();
                    log.info("Auto-registering Google OAuth user: {}", googleUser.getEmail());
                    return userRepository.save(newUser);
                });

        UserPrincipal principal = UserPrincipal.create(user);

        if (user.isTwoFaEnabled()) {
            log.info("User {} Google OAuth login successful, requiring 2FA", user.getEmail());
            String tempAccessToken = tokenProvider.generateAccessToken(principal);
            return TokenResponse.builder()
                    .accessToken(tempAccessToken)
                    .twoFaRequired(true)
                    .build();
        }

        log.info("User {} Google OAuth login successful", user.getEmail());
        UserSession session = sessionService.createSession(user, deviceFingerprint, ipAddress, userAgent);
        String accessToken = tokenProvider.generateAccessToken(principal);

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(session.getRefreshToken())
                .expiresIn(900)
                .twoFaRequired(false)
                .build();
    }

    public UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole().name())
                .twoFaEnabled(user.isTwoFaEnabled())
                .build();
    }
}

package com.cloudvault.auth.controller;

import com.cloudvault.auth.dto.*;
import com.cloudvault.auth.security.UserPrincipal;
import com.cloudvault.auth.service.AuthService;
import com.cloudvault.auth.service.SessionService;
import com.cloudvault.common.response.ApiResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;
    private final SessionService sessionService;
    private final com.cloudvault.auth.security.JwtTokenProvider tokenProvider;

    public AuthController(AuthService authService, SessionService sessionService,
                          com.cloudvault.auth.security.JwtTokenProvider tokenProvider) {
        this.authService = authService;
        this.sessionService = sessionService;
        this.tokenProvider = tokenProvider;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(@Valid @RequestBody UserRegisterRequest request) {
        UserResponse response = authService.register(request);
        return new ResponseEntity<>(ApiResponse.success(response, "User registered successfully"), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponse>> login(@Valid @RequestBody UserLoginRequest request,
                                                            HttpServletRequest servletRequest,
                                                            HttpServletResponse servletResponse) {
        String ipAddress = getClientIp(servletRequest);
        String userAgent = servletRequest.getHeader("User-Agent");

        TokenResponse tokenResponse = authService.login(request, ipAddress, userAgent);

        if (!tokenResponse.isTwoFaRequired() && tokenResponse.getRefreshToken() != null) {
            setRefreshTokenCookie(servletResponse, tokenResponse.getRefreshToken());
        }

        return ResponseEntity.ok(ApiResponse.success(tokenResponse, "Login successful"));
    }

    @PostMapping("/2fa/verify-login")
    public ResponseEntity<ApiResponse<TokenResponse>> verify2FaLogin(@Valid @RequestBody TwoFaLoginRequest request,
                                                                     HttpServletRequest servletRequest,
                                                                     HttpServletResponse servletResponse) {
        String ipAddress = getClientIp(servletRequest);
        String userAgent = servletRequest.getHeader("User-Agent");

        TokenResponse tokenResponse = authService.verify2FaLogin(
                request.getEmail(),
                request.getCode(),
                request.getDeviceFingerprint(),
                ipAddress,
                userAgent
        );

        if (tokenResponse.getRefreshToken() != null) {
            setRefreshTokenCookie(servletResponse, tokenResponse.getRefreshToken());
        }

        return ResponseEntity.ok(ApiResponse.success(tokenResponse, "2FA validation successful"));
    }

    @PostMapping("/google")
    public ResponseEntity<ApiResponse<TokenResponse>> googleLogin(@Valid @RequestBody GoogleLoginRequest request,
                                                                  HttpServletRequest servletRequest,
                                                                  HttpServletResponse servletResponse) {
        String ipAddress = getClientIp(servletRequest);
        String userAgent = servletRequest.getHeader("User-Agent");

        TokenResponse tokenResponse = authService.googleLogin(
                request.getIdToken(),
                request.getDeviceFingerprint(),
                ipAddress,
                userAgent
        );

        if (!tokenResponse.isTwoFaRequired() && tokenResponse.getRefreshToken() != null) {
            setRefreshTokenCookie(servletResponse, tokenResponse.getRefreshToken());
        }

        return ResponseEntity.ok(ApiResponse.success(tokenResponse, "Google login successful"));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenResponse>> refresh(@RequestBody(required = false) TokenRefreshRequest request,
                                                              HttpServletRequest servletRequest,
                                                              HttpServletResponse servletResponse) {
        String refreshToken = null;

        // Try to get from Cookie first
        if (servletRequest.getCookies() != null) {
            refreshToken = Arrays.stream(servletRequest.getCookies())
                    .filter(cookie -> "refreshToken".equals(cookie.getName()))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);
        }

        // Fallback to body
        if (refreshToken == null && request != null) {
            refreshToken = request.getRefreshToken();
        }

        if (refreshToken == null) {
            return new ResponseEntity<>(ApiResponse.error("Refresh token is missing"), HttpStatus.UNAUTHORIZED);
        }

        String ipAddress = getClientIp(servletRequest);
        String userAgent = servletRequest.getHeader("User-Agent");

        var updatedSession = sessionService.rotateSession(refreshToken, null, ipAddress, userAgent);
        var principal = UserPrincipal.create(updatedSession.getUser());

        String newAccessToken = tokenProvider.generateAccessToken(principal);
        setRefreshTokenCookie(servletResponse, updatedSession.getRefreshToken());

        TokenResponse tokenResponse = TokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(updatedSession.getRefreshToken())
                .expiresIn(900)
                .build();

        return ResponseEntity.ok(ApiResponse.success(tokenResponse, "Token refreshed successfully"));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest servletRequest,
                                                    HttpServletResponse servletResponse) {
        String refreshToken = null;
        if (servletRequest.getCookies() != null) {
            refreshToken = Arrays.stream(servletRequest.getCookies())
                    .filter(cookie -> "refreshToken".equals(cookie.getName()))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);
        }

        if (refreshToken != null) {
            sessionService.revokeSession(refreshToken);
        }

        Cookie cookie = new Cookie("refreshToken", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        servletResponse.addCookie(cookie);

        return ResponseEntity.ok(ApiResponse.success(null, "Logged out successfully"));
    }

    @PostMapping("/2fa/enable")
    public ResponseEntity<ApiResponse<TwoFaSetupResponse>> enable2Fa(@AuthenticationPrincipal UserPrincipal principal) {
        TwoFaSetupResponse setup = authService.enable2Fa(principal.getId());
        return ResponseEntity.ok(ApiResponse.success(setup, "2FA setup initiated"));
    }

    @PostMapping("/2fa/confirm")
    public ResponseEntity<ApiResponse<Void>> confirm2Fa(@AuthenticationPrincipal UserPrincipal principal,
                                                         @Valid @RequestBody TwoFaVerificationRequest request) {
        authService.confirm2Fa(principal.getId(), request.getCode());
        return ResponseEntity.ok(ApiResponse.success(null, "2FA enabled successfully"));
    }

    @PostMapping("/2fa/disable")
    public ResponseEntity<ApiResponse<Void>> disable2Fa(@AuthenticationPrincipal UserPrincipal principal,
                                                          @Valid @RequestBody TwoFaVerificationRequest request) {
        authService.disable2Fa(principal.getId(), request.getCode());
        return ResponseEntity.ok(ApiResponse.success(null, "2FA disabled successfully"));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(@AuthenticationPrincipal UserPrincipal principal) {
        UserResponse userResponse = UserResponse.builder()
                .id(principal.getId())
                .email(principal.getEmail())
                .firstName("")
                .lastName("")
                .role(principal.getAuthorities().stream().findFirst().map(r -> r.getAuthority()).orElse("ROLE_USER"))
                .twoFaEnabled(principal.isTwoFaEnabled())
                .build();
        return ResponseEntity.ok(ApiResponse.success(userResponse, "Current user fetched"));
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    private void setRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(7 * 24 * 60 * 60);
        response.addCookie(cookie);
    }
}

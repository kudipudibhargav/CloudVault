package com.cloudvault.auth.service;

import dev.samstevens.totp.code.CodeGenerator;
import dev.samstevens.totp.code.CodeVerifier;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.exceptions.QrGenerationException;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.QrGenerator;
import dev.samstevens.totp.qr.ZxingPngQrGenerator;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.time.TimeProvider;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class TwoFactorAuthService {

    private final SecretGenerator secretGenerator;
    private final CodeVerifier codeVerifier;

    public TwoFactorAuthService() {
        this.secretGenerator = new DefaultSecretGenerator();
        TimeProvider timeProvider = new SystemTimeProvider();
        CodeGenerator codeGenerator = new DefaultCodeGenerator();
        // DefaultCodeVerifier uses 1 step (30 seconds) window allowance by default
        this.codeVerifier = new DefaultCodeVerifier(codeGenerator, timeProvider);
    }

    public String generateNewSecret() {
        return secretGenerator.generate();
    }

    public String getQrCodeUrl(String email, String secret) {
        String appName = "CloudVault";
        return String.format(
                "otpauth://totp/%s:%s?secret=%s&issuer=%s",
                URLEncoder.encode(appName, StandardCharsets.UTF_8),
                URLEncoder.encode(email, StandardCharsets.UTF_8),
                secret,
                URLEncoder.encode(appName, StandardCharsets.UTF_8)
        );
    }

    public boolean verifyCode(String secret, String code) {
        return codeVerifier.isValidCode(secret, code);
    }
}

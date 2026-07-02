package com.cloudvault.auth.controller;

import com.cloudvault.auth.dto.TwoFaVerificationRequest;
import com.cloudvault.auth.dto.UserLoginRequest;
import com.cloudvault.auth.dto.UserRegisterRequest;
import com.cloudvault.auth.model.User;
import com.cloudvault.auth.model.UserRole;
import com.cloudvault.auth.repository.UserRepository;
import com.cloudvault.auth.repository.UserSessionRepository;
import com.cloudvault.auth.service.TwoFactorAuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("local-h2")
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserSessionRepository userSessionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RedisTemplate<String, Object> redisTemplate;

    @MockBean
    private ValueOperations<String, Object> valueOperations;

    @MockBean
    private TwoFactorAuthService twoFactorAuthService;

    @BeforeEach
    public void setup() {
        userSessionRepository.deleteAll();
        userRepository.deleteAll();
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    public void testUserRegistration_Success() throws Exception {
        UserRegisterRequest request = new UserRegisterRequest();
        request.setEmail("john.doe@example.com");
        request.setPassword("securePassword123");
        request.setFirstName("John");
        request.setLastName("Doe");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", containsString("registered successfully")))
                .andExpect(jsonPath("$.data.email", is("john.doe@example.com")))
                .andExpect(jsonPath("$.data.role", is("ROLE_USER")))
                .andExpect(jsonPath("$.data.twoFaEnabled", is(false)));
    }

    @Test
    public void testUserRegistration_ValidationFailure() throws Exception {
        UserRegisterRequest request = new UserRegisterRequest();
        request.setEmail("invalid-email");
        request.setPassword("123");
        request.setFirstName("");
        request.setLastName("Doe");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("Validation failed")))
                .andExpect(jsonPath("$.errors.email", notNullValue()))
                .andExpect(jsonPath("$.errors.password", notNullValue()))
                .andExpect(jsonPath("$.errors.firstName", notNullValue()));
    }

    @Test
    public void testUserRegistration_DuplicateEmail() throws Exception {
        User existingUser = User.builder()
                .email("duplicate@example.com")
                .passwordHash(passwordEncoder.encode("password"))
                .firstName("Existing")
                .lastName("User")
                .role(UserRole.ROLE_USER)
                .build();
        userRepository.save(existingUser);

        UserRegisterRequest request = new UserRegisterRequest();
        request.setEmail("duplicate@example.com");
        request.setPassword("securePassword123");
        request.setFirstName("John");
        request.setLastName("Doe");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", containsString("already exists")));
    }

    @Test
    public void testUserLogin_Success_No2FA() throws Exception {
        User user = User.builder()
                .email("login@example.com")
                .passwordHash(passwordEncoder.encode("password123"))
                .firstName("Login")
                .lastName("User")
                .role(UserRole.ROLE_USER)
                .twoFaEnabled(false)
                .build();
        userRepository.save(user);

        UserLoginRequest request = new UserLoginRequest();
        request.setEmail("login@example.com");
        request.setPassword("password123");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.accessToken", notNullValue()))
                .andExpect(jsonPath("$.data.refreshToken", notNullValue()))
                .andExpect(jsonPath("$.data.twoFaRequired", is(false)));
    }

    @Test
    public void testUserLogin_WrongPassword() throws Exception {
        User user = User.builder()
                .email("wrong@example.com")
                .passwordHash(passwordEncoder.encode("password123"))
                .firstName("Wrong")
                .lastName("User")
                .role(UserRole.ROLE_USER)
                .build();
        userRepository.save(user);

        UserLoginRequest request = new UserLoginRequest();
        request.setEmail("wrong@example.com");
        request.setPassword("wrongPassword");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success", is(false)));
    }

    @Test
    public void testUserLogin_Success_With2FA() throws Exception {
        User user = User.builder()
                .email("2fa@example.com")
                .passwordHash(passwordEncoder.encode("password123"))
                .firstName("TwoFa")
                .lastName("User")
                .role(UserRole.ROLE_USER)
                .twoFaEnabled(true)
                .twoFaSecret("SECRETKEY123")
                .build();
        userRepository.save(user);

        UserLoginRequest request = new UserLoginRequest();
        request.setEmail("2fa@example.com");
        request.setPassword("password123");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.accessToken", notNullValue()))
                .andExpect(jsonPath("$.data.refreshToken", nullValue()))
                .andExpect(jsonPath("$.data.twoFaRequired", is(true)));
    }
}

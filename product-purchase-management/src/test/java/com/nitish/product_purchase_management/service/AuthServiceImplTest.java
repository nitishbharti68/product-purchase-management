package com.nitish.product_purchase_management.service;

import com.nitish.product_purchase_management.dto.request.LoginRequest;
import com.nitish.product_purchase_management.dto.request.RegisterRequest;
import com.nitish.product_purchase_management.dto.response.LoginResponse;
import com.nitish.product_purchase_management.dto.response.RegisterResponse;
import com.nitish.product_purchase_management.entity.Role;
import com.nitish.product_purchase_management.entity.User;
import com.nitish.product_purchase_management.exception.DuplicateEmailException;
import com.nitish.product_purchase_management.exception.InvalidCredentialsException;
import com.nitish.product_purchase_management.repository.UserRepository;
import com.nitish.product_purchase_management.security.JwtService;
import com.nitish.product_purchase_management.service.impl.AuthServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {

        @Mock
        private UserRepository userRepository;

        @Mock
        private PasswordEncoder passwordEncoder;

        @Mock
        private JwtService jwtService;

        @InjectMocks
        private AuthServiceImpl authService;

        private User existingUser;

        @BeforeEach
        void setUp() {

                existingUser = new User();

                existingUser.setUserId(1L);
                existingUser.setFirstName("Nitish");
                existingUser.setLastName("Bharti");
                existingUser.setEmail("nitish@test.com");
                existingUser.setPasswordHash("encoded-password");
                existingUser.setRole(Role.END_USER);
        }

        @Test
        @DisplayName("Registration should save a new user "
                        + "with END_USER role")
        void registerShouldSaveNewUser() {

                RegisterRequest request = createRegisterRequest();

                when(
                                userRepository.existsByEmail(
                                                "nitish@test.com"))
                                .thenReturn(false);

                when(
                                passwordEncoder.encode(
                                                "Password@123"))
                                .thenReturn("encoded-password");

                when(
                                userRepository.save(
                                                any(User.class)))
                                .thenAnswer(invocation -> {

                                        User user = invocation.getArgument(0);

                                        user.setUserId(1L);

                                        return user;
                                });

                RegisterResponse response = authService.register(request);

                assertThat(response).isNotNull();

                assertThat(
                                response.getUserId()).isEqualTo(1L);

                assertThat(
                                response.getEmail()).isEqualTo("nitish@test.com");

                assertThat(
                                response.getRole()).isEqualTo("END_USER");

                verify(
                                passwordEncoder).encode("Password@123");

                verify(
                                userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("Registration should normalize email "
                        + "before saving")
        void registerShouldNormalizeEmail() {

                RegisterRequest request = createRegisterRequest();

                request.setEmail(
                                "  NITISH@TEST.COM  ");

                when(
                                userRepository.existsByEmail(
                                                "nitish@test.com"))
                                .thenReturn(false);

                when(
                                passwordEncoder.encode(
                                                request.getPassword()))
                                .thenReturn("encoded-password");

                when(
                                userRepository.save(
                                                any(User.class)))
                                .thenAnswer(invocation -> {

                                        User user = invocation.getArgument(0);

                                        user.setUserId(1L);

                                        return user;
                                });

                RegisterResponse response = authService.register(request);

                assertThat(
                                response.getEmail()).isEqualTo("nitish@test.com");
        }

        @Test
        @DisplayName("Registration should reject "
                        + "an existing email")
        void registerShouldRejectDuplicateEmail() {

                RegisterRequest request = createRegisterRequest();

                when(
                                userRepository.existsByEmail(
                                                "nitish@test.com"))
                                .thenReturn(true);

                assertThatThrownBy(
                                () -> authService.register(request))
                                .isInstanceOf(
                                                DuplicateEmailException.class)
                                .hasMessage(
                                                "Email already registered");

                verify(
                                passwordEncoder,
                                never()).encode(any());

                verify(
                                userRepository,
                                never()).save(any(User.class));
        }

        @Test
        @DisplayName("Registration should reject "
                        + "mismatched passwords")
        void registerShouldRejectPasswordMismatch() {

                RegisterRequest request = createRegisterRequest();

                request.setConfirmPassword(
                                "DifferentPassword@123");

                when(
                                userRepository.existsByEmail(
                                                "nitish@test.com"))
                                .thenReturn(false);

                assertThatThrownBy(
                                () -> authService.register(request))
                                .isInstanceOf(
                                                IllegalArgumentException.class)
                                .hasMessage(
                                                "Passwords do not match");

                verify(
                                userRepository,
                                never()).save(any(User.class));
        }

        @Test
        @DisplayName("Login should return a JWT "
                        + "for valid credentials")
        void loginShouldReturnToken() {

                LoginRequest request = createLoginRequest();

                when(
                                userRepository.findByEmail(
                                                "nitish@test.com"))
                                .thenReturn(
                                                Optional.of(existingUser));

                when(
                                passwordEncoder.matches(
                                                "Password@123",
                                                "encoded-password"))
                                .thenReturn(true);

                when(
                                jwtService.generateToken(
                                                any(UserDetails.class)))
                                .thenReturn("generated-jwt-token");

                LoginResponse response = authService.login(request);

                assertThat(response).isNotNull();

                assertThat(
                                response.getToken()).isEqualTo("generated-jwt-token");

                assertThat(
                                response.getTokenType()).isEqualTo("Bearer");

                verify(
                                jwtService).generateToken(
                                                any(UserDetails.class));
        }

        @Test
        @DisplayName("Login should reject an unknown email")
        void loginShouldRejectUnknownEmail() {

                LoginRequest request = createLoginRequest();

                when(
                                userRepository.findByEmail(
                                                "nitish@test.com"))
                                .thenReturn(Optional.empty());

                assertThatThrownBy(
                                () -> authService.login(request))
                                .isInstanceOf(
                                                InvalidCredentialsException.class)
                                .hasMessage(
                                                "Invalid email or password");

                verify(
                                passwordEncoder,
                                never()).matches(any(), any());

                verify(
                                jwtService,
                                never()).generateToken(
                                                any(UserDetails.class));
        }

        @Test
        @DisplayName("Login should reject "
                        + "an incorrect password")
        void loginShouldRejectIncorrectPassword() {

                LoginRequest request = createLoginRequest();

                when(
                                userRepository.findByEmail(
                                                "nitish@test.com"))
                                .thenReturn(
                                                Optional.of(existingUser));

                when(
                                passwordEncoder.matches(
                                                "Password@123",
                                                "encoded-password"))
                                .thenReturn(false);

                assertThatThrownBy(
                                () -> authService.login(request))
                                .isInstanceOf(
                                                InvalidCredentialsException.class)
                                .hasMessage(
                                                "Invalid email or password");

                verify(
                                jwtService,
                                never()).generateToken(
                                                any(UserDetails.class));
        }

        private RegisterRequest createRegisterRequest() {

                RegisterRequest request = new RegisterRequest();

                request.setFirstName("Nitish");
                request.setLastName("Bharti");
                request.setEmail("nitish@test.com");
                request.setPassword("Password@123");

                request.setConfirmPassword(
                                "Password@123");

                return request;
        }

        private LoginRequest createLoginRequest() {

                LoginRequest request = new LoginRequest();

                request.setEmail("nitish@test.com");
                request.setPassword("Password@123");

                return request;
        }
}
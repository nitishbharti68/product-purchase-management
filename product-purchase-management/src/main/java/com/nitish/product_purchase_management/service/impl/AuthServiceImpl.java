package com.nitish.product_purchase_management.service.impl;

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
import com.nitish.product_purchase_management.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.nitish.product_purchase_management.security.JwtService;
import org.springframework.security.core.userdetails.UserDetails;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

        private final UserRepository userRepository;

        private final PasswordEncoder passwordEncoder;

        private final JwtService jwtService;

        @Override
        public RegisterResponse register(RegisterRequest request) {

                String email = request.getEmail().trim().toLowerCase();

                if (userRepository.existsByEmail(email)) {
                        throw new DuplicateEmailException(
                                        "Email already registered");
                }

                if (!request.getPassword().equals(
                                request.getConfirmPassword())) {

                        throw new IllegalArgumentException(
                                        "Passwords do not match");
                }

                User user = new User();

                user.setFirstName(request.getFirstName());
                user.setLastName(request.getLastName());

                user.setEmail(email);

                user.setPasswordHash(
                                passwordEncoder.encode(request.getPassword()));

                user.setRole(Role.END_USER);

                User savedUser = userRepository.save(user);

                return new RegisterResponse(
                                savedUser.getUserId(),
                                savedUser.getFirstName(),
                                savedUser.getLastName(),
                                savedUser.getEmail(),
                                savedUser.getRole().name());
        }

        @Override
        public LoginResponse login(LoginRequest request) {

                String email = request.getEmail()
                                .trim()
                                .toLowerCase();

                User user = userRepository
                                .findByEmail(email)
                                .orElseThrow(() -> new InvalidCredentialsException(
                                                "Invalid email or password"));

                boolean passwordMatches = passwordEncoder.matches(
                                request.getPassword(),
                                user.getPasswordHash());

                if (!passwordMatches) {
                        throw new InvalidCredentialsException(
                                        "Invalid email or password");
                }

                UserDetails userDetails = org.springframework.security.core.userdetails.User
                                .withUsername(user.getEmail())
                                .password(user.getPasswordHash())
                                .authorities(
                                                "ROLE_" + user.getRole().name())
                                .build();

                String token = jwtService.generateToken(userDetails);

                return new LoginResponse(
                                token,
                                "Bearer",
                                user.getRole().name(),
                                user.getFirstName());
        }
}
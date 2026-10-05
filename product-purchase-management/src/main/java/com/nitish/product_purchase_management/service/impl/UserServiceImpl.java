package com.nitish.product_purchase_management.service.impl;

import com.nitish.product_purchase_management.dto.request.ChangePasswordRequest;
import com.nitish.product_purchase_management.dto.response.MessageResponse;
import com.nitish.product_purchase_management.dto.response.UserResponse;
import com.nitish.product_purchase_management.entity.User;
import com.nitish.product_purchase_management.exception.PasswordChangeException;
import com.nitish.product_purchase_management.exception.ResourceNotFoundException;
import com.nitish.product_purchase_management.repository.UserRepository;
import com.nitish.product_purchase_management.service.UserService;

import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl
        implements UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getCurrentUser(
            String email) {

        User user = getUserByEmail(email);

        return mapToUserResponse(user);
    }

    @Override
    @Transactional
    public MessageResponse changePassword(
            ChangePasswordRequest request,
            String email) {

        User user = getUserByEmail(email);

        boolean currentPasswordMatches = passwordEncoder.matches(
                request.getCurrentPassword(),
                user.getPasswordHash());

        if (!currentPasswordMatches) {

            throw new PasswordChangeException(
                    "Current password is incorrect");
        }

        if (!request.getNewPassword()
                .equals(
                        request.getConfirmNewPassword())) {

            throw new PasswordChangeException(
                    "New password and confirm "
                            + "new password do not match");
        }

        boolean newPasswordSameAsCurrent = passwordEncoder.matches(
                request.getNewPassword(),
                user.getPasswordHash());

        if (newPasswordSameAsCurrent) {

            throw new PasswordChangeException(
                    "New password cannot be the "
                            + "same as the current password");
        }

        String encodedNewPassword = passwordEncoder.encode(
                request.getNewPassword());

        user.setPasswordHash(
                encodedNewPassword);

        userRepository.save(user);

        return new MessageResponse(
                "Password changed successfully");
    }

    private User getUserByEmail(
            String email) {

        String normalizedEmail = email.trim().toLowerCase();

        return userRepository
                .findByEmail(normalizedEmail)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Authenticated user not found"));
    }

    private UserResponse mapToUserResponse(
            User user) {

        return new UserResponse(
                user.getUserId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRole().name());
    }
}
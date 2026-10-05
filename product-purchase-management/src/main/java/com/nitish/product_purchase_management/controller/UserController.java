package com.nitish.product_purchase_management.controller;

import com.nitish.product_purchase_management.dto.request.ChangePasswordRequest;
import com.nitish.product_purchase_management.dto.response.MessageResponse;
import com.nitish.product_purchase_management.dto.response.UserResponse;
import com.nitish.product_purchase_management.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Tag(name = "2. User", description = "Current user profile and password change")
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Get my profile", description = "The user is identified from the JWT.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile returned"),
            @ApiResponse(responseCode = "401", description = "Token missing, invalid or expired", content = @Content)
    })
    @GetMapping("/me")
    public UserResponse getCurrentUser(
            Authentication authentication) {

        return userService.getCurrentUser(
                authentication.getName());
    }

    @Operation(summary = "Change my password", description = "Current password must be correct. "
            + "New password must meet the policy and differ "
            + "from the current one.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Password changed"),
            @ApiResponse(responseCode = "400", description = "Wrong current password, policy not met, "
                    + "mismatch or password reused", content = @Content),
            @ApiResponse(responseCode = "401", description = "Token missing, invalid or expired", content = @Content)
    })
    @PutMapping("/me/password")
    public MessageResponse changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            Authentication authentication) {

        return userService.changePassword(
                request,
                authentication.getName());
    }
}
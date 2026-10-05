package com.nitish.product_purchase_management.controller;

import com.nitish.product_purchase_management.dto.request.LoginRequest;
import com.nitish.product_purchase_management.dto.request.RegisterRequest;
import com.nitish.product_purchase_management.dto.response.LoginResponse;
import com.nitish.product_purchase_management.dto.response.RegisterResponse;
import com.nitish.product_purchase_management.service.AuthService;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "1. Authentication", description = "Register a new account and log in to receive a JWT")
@SecurityRequirements // empty = these endpoints need NO token
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Register a new user", description = "Creates an account with role END_USER. "
            + "Any role sent by the client is ignored.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User registered"),
            @ApiResponse(responseCode = "400", description = "Validation failed or passwords do not match", content = @Content),
            @ApiResponse(responseCode = "409", description = "Email already registered", content = @Content)
    })
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public RegisterResponse register(
            @Valid @RequestBody RegisterRequest request) {

        return authService.register(request);
    }

    @Operation(summary = "Log in", description = "Returns a JWT valid for 30 minutes, "
            + "plus the user's role and first name.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "400", description = "Email or password missing", content = @Content),
            @ApiResponse(responseCode = "401", description = "Invalid email or password", content = @Content)
    })
    @PostMapping("/login")
    public LoginResponse login(
            @Valid @RequestBody LoginRequest request) {

        return authService.login(request);
    }

    @Hidden // debug endpoint: keep it out of the documentation
    @GetMapping("/test")
    public String test() {
        return "Controller Working";
    }
}
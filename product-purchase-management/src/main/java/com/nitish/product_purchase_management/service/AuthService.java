package com.nitish.product_purchase_management.service;

import com.nitish.product_purchase_management.dto.request.LoginRequest;
import com.nitish.product_purchase_management.dto.request.RegisterRequest;
import com.nitish.product_purchase_management.dto.response.LoginResponse;
import com.nitish.product_purchase_management.dto.response.RegisterResponse;

public interface AuthService {

    RegisterResponse register(RegisterRequest request);
    

    LoginResponse login(LoginRequest request);
}
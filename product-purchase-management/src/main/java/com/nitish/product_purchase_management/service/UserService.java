package com.nitish.product_purchase_management.service;

import com.nitish.product_purchase_management.dto.request.ChangePasswordRequest;
import com.nitish.product_purchase_management.dto.response.MessageResponse;
import com.nitish.product_purchase_management.dto.response.UserResponse;

public interface UserService {

    UserResponse getCurrentUser(
            String email);

    MessageResponse changePassword(
            ChangePasswordRequest request,
            String email);
}
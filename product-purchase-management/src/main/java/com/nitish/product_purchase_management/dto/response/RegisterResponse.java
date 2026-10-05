package com.nitish.product_purchase_management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegisterResponse {

    private Long userId;

    private String firstName;

    private String lastName;

    private String email;

    private String role;
}
package com.nitish.product_purchase_management.dto.response;

public class LoginResponse {

    private String token;

    private String tokenType;

    private String role;

    private String firstName;

    public LoginResponse() {
    }

    public LoginResponse(
            String token,
            String tokenType,
            String role,
            String firstName) {
        this.token = token;
        this.tokenType = tokenType;
        this.role = role;
        this.firstName = firstName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(
            String token) {
        this.token = token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(
            String tokenType) {
        this.tokenType = tokenType;
    }

    public String getRole() {
        return role;
    }

    public void setRole(
            String role) {
        this.role = role;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(
            String firstName) {
        this.firstName = firstName;
    }
}
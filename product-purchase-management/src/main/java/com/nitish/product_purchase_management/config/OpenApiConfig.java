package com.nitish.product_purchase_management.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(title = "Product Purchase Management API", version = "v1", description = "REST API for registration, login, "
                + "product browsing, purchasing, purchase history "
                + "and admin product management.", contact = @Contact(name = "Nitish Kumar Bharti")), security = @SecurityRequirement(name = "bearerAuth"))
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT", description = "Paste the token from /api/v1/auth/login. "
                + "Do not type the word Bearer.")
public class OpenApiConfig {
}
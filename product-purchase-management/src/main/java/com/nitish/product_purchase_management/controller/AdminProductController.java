package com.nitish.product_purchase_management.controller;

import com.nitish.product_purchase_management.dto.request.ProductRequest;
import com.nitish.product_purchase_management.dto.request.ProductStatusRequest;
import com.nitish.product_purchase_management.dto.response.PageResponse;
import com.nitish.product_purchase_management.dto.response.ProductResponse;
import com.nitish.product_purchase_management.service.ProductService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Tag(name = "5. Admin - Products", description = "Add, list, edit, activate and deactivate products. "
                + "Requires role ADMIN.")
@ApiResponses({
                // Apply to every method in this controller
                @ApiResponse(responseCode = "401", description = "Token missing, invalid or expired", content = @Content),
                @ApiResponse(responseCode = "403", description = "Caller is not ADMIN", content = @Content)
})
@RestController
@RequestMapping("/api/v1/admin/products")
@PreAuthorize("hasRole('ADMIN')")
public class AdminProductController {

        private final ProductService productService;

        public AdminProductController(ProductService productService) {
                this.productService = productService;
        }

        @Operation(summary = "Add a product", description = "active is set to true and createdBy is the "
                        + "admin from the token.")
        @ApiResponses({
                        @ApiResponse(responseCode = "201", description = "Product created"),
                        @ApiResponse(responseCode = "400", description = "Validation failed "
                                        + "(e.g. imagePath over 500 characters)", content = @Content)
        })
        @PostMapping
        @ResponseStatus(HttpStatus.CREATED)
        public ProductResponse addProduct(
                        @Valid @RequestBody ProductRequest request,
                        Authentication authentication) {

                return productService.addProduct(
                                request,
                                authentication.getName());
        }

        @Operation(summary = "List all products", description = "Includes inactive products. Newest first.")
        @ApiResponse(responseCode = "200", description = "Page of products")
        @GetMapping
        public PageResponse<ProductResponse> getAllProducts(

                        @Parameter(description = "Case-insensitive product name search") @RequestParam(required = false) String search,

                        @Parameter(description = "Zero-based page number", example = "0") @RequestParam(defaultValue = "0") int page,

                        @Parameter(description = "Page size (1 to 100)", example = "10") @RequestParam(defaultValue = "10") int size) {

                int safeSize = Math.min(Math.max(size, 1), 100);

                Pageable pageable = PageRequest.of(
                                Math.max(page, 0),
                                safeSize,
                                Sort.by(Sort.Direction.DESC, "createdDate"));

                return productService.getAllProductsForAdmin(search, pageable);
        }

        @Operation(summary = "Update a product", description = "createdBy, createdDate and active are not changed.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Product updated"),
                        @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content),
                        @ApiResponse(responseCode = "404", description = "Product not found", content = @Content),
                        @ApiResponse(responseCode = "409", description = "Product changed by another request", content = @Content)
        })
        @PutMapping("/{productId}")
        public ProductResponse updateProduct(

                        @Parameter(description = "Product ID", example = "1") @PathVariable Long productId,

                        @Valid @RequestBody ProductRequest request) {

                return productService.updateProduct(productId, request);
        }

        @Operation(summary = "Activate or deactivate a product", description = "Inactive products disappear from the buyer "
                        + "catalogue but stay in purchase history.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Status updated"),
                        @ApiResponse(responseCode = "400", description = "active missing", content = @Content),
                        @ApiResponse(responseCode = "404", description = "Product not found", content = @Content)
        })
        @PatchMapping("/{productId}/status")
        public ProductResponse updateProductStatus(

                        @Parameter(description = "Product ID", example = "1") @PathVariable Long productId,

                        @Valid @RequestBody ProductStatusRequest request) {

                return productService.updateProductStatus(
                                productId,
                                request.getActive());
        }
}
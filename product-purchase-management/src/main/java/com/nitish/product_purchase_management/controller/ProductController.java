package com.nitish.product_purchase_management.controller;

import com.nitish.product_purchase_management.dto.response.PageResponse;
import com.nitish.product_purchase_management.dto.response.ProductResponse;
import com.nitish.product_purchase_management.service.ProductService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@Tag(name = "3. Products", description = "Browse active products (buyer view)")
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

        private final ProductService productService;

        public ProductController(ProductService productService) {
                this.productService = productService;
        }

        @Operation(summary = "List active products", description = "Paginated, sorted by product name. "
                        + "Inactive products are never returned.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Page of products"),
                        @ApiResponse(responseCode = "401", description = "Token missing, invalid or expired", content = @Content)
        })
        @GetMapping
        public PageResponse<ProductResponse> getActiveProducts(

                        @Parameter(description = "Case-insensitive product name search", example = "lap") @RequestParam(required = false) String search,

                        @Parameter(description = "Zero-based page number", example = "0") @RequestParam(defaultValue = "0") int page,

                        @Parameter(description = "Page size (1 to 100)", example = "10") @RequestParam(defaultValue = "10") int size) {

                int safeSize = Math.min(Math.max(size, 1), 100);

                Pageable pageable = PageRequest.of(
                                Math.max(page, 0),
                                safeSize,
                                Sort.by(Sort.Direction.ASC, "productName"));

                return productService.getActiveProducts(search, pageable);
        }

        @Operation(summary = "Get one active product")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Product found"),
                        @ApiResponse(responseCode = "401", description = "Token missing, invalid or expired", content = @Content),
                        @ApiResponse(responseCode = "404", description = "Product not found or inactive", content = @Content)
        })
        @GetMapping("/{productId}")
        public ProductResponse getProduct(

                        @Parameter(description = "Product ID", example = "1") @PathVariable Long productId) {

                return productService.getActiveProduct(productId);
        }
}
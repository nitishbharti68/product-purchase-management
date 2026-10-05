package com.nitish.product_purchase_management.controller;

import com.nitish.product_purchase_management.dto.request.PurchaseRequest;
import com.nitish.product_purchase_management.dto.response.PageResponse;
import com.nitish.product_purchase_management.dto.response.PurchaseResponse;
import com.nitish.product_purchase_management.service.PurchaseService;

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
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Tag(name = "4. Purchases", description = "Buy products and view your own purchase history")
@RestController
@RequestMapping("/api/v1/purchases")
public class PurchaseController {

        private final PurchaseService purchaseService;

        public PurchaseController(PurchaseService purchaseService) {
                this.purchaseService = purchaseService;
        }

        @Operation(summary = "Purchase a product", description = "Send only productId and quantity. The server "
                        + "reads the price, calculates the total, saves the "
                        + "purchase and reduces stock in one transaction.")
        @ApiResponses({
                        @ApiResponse(responseCode = "201", description = "Purchase completed"),
                        @ApiResponse(responseCode = "400", description = "productId or quantity missing, "
                                        + "or quantity below 1", content = @Content),
                        @ApiResponse(responseCode = "401", description = "Token missing, invalid or expired", content = @Content),
                        @ApiResponse(responseCode = "404", description = "Product not found", content = @Content),
                        @ApiResponse(responseCode = "409", description = "Product inactive, insufficient stock, "
                                        + "or stock changed by another purchase", content = @Content)
        })
        @PostMapping
        @ResponseStatus(HttpStatus.CREATED)
        public PurchaseResponse purchase(
                        @Valid @RequestBody PurchaseRequest request,
                        Authentication authentication) {

                return purchaseService.purchase(
                                request,
                                authentication.getName());
        }

        @Operation(summary = "My purchase history", description = "Only your own purchases, newest first. "
                        + "fromDate and toDate must be given together.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Page of purchases"),
                        @ApiResponse(responseCode = "400", description = "Incomplete or reversed date range", content = @Content),
                        @ApiResponse(responseCode = "401", description = "Token missing, invalid or expired", content = @Content)
        })
        @GetMapping("/me")
        public PageResponse<PurchaseResponse> getMyPurchases(

                        @Parameter(description = "Filter by product name", example = "laptop") @RequestParam(required = false) String productName,

                        @Parameter(description = "From date (yyyy-MM-dd)", example = "2026-09-01") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,

                        @Parameter(description = "To date (yyyy-MM-dd)", example = "2026-09-30") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,

                        @Parameter(description = "Zero-based page number", example = "0") @RequestParam(defaultValue = "0") int page,

                        @Parameter(description = "Page size (1 to 100)", example = "10") @RequestParam(defaultValue = "10") int size,

                        Authentication authentication) {

                int safePage = Math.max(page, 0);
                int safeSize = Math.min(Math.max(size, 1), 100);

                Pageable pageable = PageRequest.of(
                                safePage,
                                safeSize,
                                Sort.by(Sort.Direction.DESC, "purchaseDate"));

                return purchaseService.getMyPurchases(
                                authentication.getName(),
                                productName,
                                fromDate,
                                toDate,
                                pageable);
        }

        @Operation(summary = "Get one of my purchases", description = "Returns 404 (not 403) if the purchase belongs "
                        + "to another user, so its existence is not revealed.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Purchase found"),
                        @ApiResponse(responseCode = "401", description = "Token missing, invalid or expired", content = @Content),
                        @ApiResponse(responseCode = "404", description = "Not found or not yours", content = @Content)
        })
        @GetMapping("/me/{purchaseId}")
        public PurchaseResponse getMyPurchaseById(

                        @Parameter(description = "Purchase ID", example = "1") @PathVariable Long purchaseId,

                        Authentication authentication) {

                return purchaseService.getMyPurchaseById(
                                purchaseId,
                                authentication.getName());
        }
}
package com.nitish.product_purchase_management.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class PurchaseResponse {

    private Long purchaseId;
    private LocalDateTime purchaseDate;
    private BigDecimal totalAmount;
    private List<PurchaseItemResponse> items;
    private String message;

    public PurchaseResponse() {
    }

    public PurchaseResponse(
            Long purchaseId,
            LocalDateTime purchaseDate,
            BigDecimal totalAmount,
            List<PurchaseItemResponse> items,
            String message) {
        this.purchaseId = purchaseId;
        this.purchaseDate = purchaseDate;
        this.totalAmount = totalAmount;
        this.items = items;
        this.message = message;
    }

    public Long getPurchaseId() {
        return purchaseId;
    }

    public LocalDateTime getPurchaseDate() {
        return purchaseDate;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public List<PurchaseItemResponse> getItems() {
        return items;
    }

    public String getMessage() {
        return message;
    }
}
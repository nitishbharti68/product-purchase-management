package com.nitish.product_purchase_management.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ProductResponse {

    private Long productId;
    private String productName;
    private String description;
    private BigDecimal price;
    private Integer quantity;
    private String category;
    private String imagePath;
    private Boolean active;
    private LocalDateTime createdDate;

    public ProductResponse() {
    }

    public ProductResponse(
            Long productId,
            String productName,
            String description,
            BigDecimal price,
            Integer quantity,
            String category,
            String imagePath,
            Boolean active,
            LocalDateTime createdDate) {
        this.productId = productId;
        this.productName = productName;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
        this.category = category;
        this.imagePath = imagePath;
        this.active = active;
        this.createdDate = createdDate;
    }

    public Long getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public String getCategory() {
        return category;
    }

    public String getImagePath() {
        return imagePath;
    }

    public Boolean getActive() {
        return active;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }
}
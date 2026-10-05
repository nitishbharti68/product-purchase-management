package com.nitish.product_purchase_management.service;

import com.nitish.product_purchase_management.dto.request.ProductRequest;
import com.nitish.product_purchase_management.dto.response.PageResponse;
import com.nitish.product_purchase_management.dto.response.ProductResponse;

import org.springframework.data.domain.Pageable;

public interface ProductService {

        ProductResponse addProduct(
                        ProductRequest request,
                        String adminEmail);

        PageResponse<ProductResponse> getActiveProducts(
                        String search,
                        Pageable pageable);

        ProductResponse getActiveProduct(
                        Long productId);

        PageResponse<ProductResponse> getAllProductsForAdmin(
                        String search,
                        Pageable pageable);

        ProductResponse updateProduct(
                        Long productId,
                        ProductRequest request);

        ProductResponse updateProductStatus(
                        Long productId,
                        Boolean active);
}

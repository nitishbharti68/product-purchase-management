package com.nitish.product_purchase_management.service;

import com.nitish.product_purchase_management.dto.request.PurchaseRequest;
import com.nitish.product_purchase_management.dto.response.PageResponse;
import com.nitish.product_purchase_management.dto.response.PurchaseResponse;

import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface PurchaseService {

    PurchaseResponse purchase(
            PurchaseRequest request,
            String userEmail);

    PageResponse<PurchaseResponse> getMyPurchases(
            String userEmail,
            String productName,
            LocalDate fromDate,
            LocalDate toDate,
            Pageable pageable);

    PurchaseResponse getMyPurchaseById(
            Long purchaseId,
            String userEmail);
}
package com.nitish.product_purchase_management.service.impl;

import com.nitish.product_purchase_management.dto.request.PurchaseRequest;
import com.nitish.product_purchase_management.dto.response.PageResponse;
import com.nitish.product_purchase_management.dto.response.PurchaseItemResponse;
import com.nitish.product_purchase_management.dto.response.PurchaseResponse;
import com.nitish.product_purchase_management.entity.Product;
import com.nitish.product_purchase_management.entity.Purchase;
import com.nitish.product_purchase_management.entity.PurchaseDetail;
import com.nitish.product_purchase_management.entity.User;
import com.nitish.product_purchase_management.exception.InsufficientStockException;
import com.nitish.product_purchase_management.exception.ProductInactiveException;
import com.nitish.product_purchase_management.exception.ResourceNotFoundException;
import com.nitish.product_purchase_management.repository.ProductRepository;
import com.nitish.product_purchase_management.repository.PurchaseDetailRepository;
import com.nitish.product_purchase_management.repository.PurchaseRepository;
import com.nitish.product_purchase_management.repository.UserRepository;
import com.nitish.product_purchase_management.service.PurchaseService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.nitish.product_purchase_management.dto.response.PageResponse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class PurchaseServiceImpl implements PurchaseService {

    private final UserRepository userRepository;

    private final ProductRepository productRepository;

    private final PurchaseRepository purchaseRepository;

    private final PurchaseDetailRepository purchaseDetailRepository;

    public PurchaseServiceImpl(
            UserRepository userRepository,
            ProductRepository productRepository,
            PurchaseRepository purchaseRepository,
            PurchaseDetailRepository purchaseDetailRepository) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.purchaseRepository = purchaseRepository;
        this.purchaseDetailRepository = purchaseDetailRepository;
    }

    @Override
    @Transactional
    public PurchaseResponse purchase(
            PurchaseRequest request,
            String userEmail) {

        String normalizedEmail = userEmail.trim().toLowerCase();

        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Authenticated user not found"));

        Product product = productRepository.findById(
                request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found"));

        if (!Boolean.TRUE.equals(product.getActive())) {

            throw new ProductInactiveException(
                    "Product is not available for purchase");
        }

        if (product.getQuantity() < request.getQuantity()) {

            throw new InsufficientStockException(
                    "Only "
                            + product.getQuantity()
                            + " item(s) are available");
        }

        /*
         * Read the price from the database.
         * Never trust the client for price values.
         */
        BigDecimal unitPrice = product.getPrice();

        BigDecimal totalAmount = unitPrice.multiply(
                BigDecimal.valueOf(
                        request.getQuantity()));

        Purchase purchase = new Purchase();

        purchase.setUser(user);
        purchase.setTotalAmount(totalAmount);

        Purchase savedPurchase = purchaseRepository.save(purchase);

        PurchaseDetail purchaseDetail = new PurchaseDetail();

        purchaseDetail.setPurchase(savedPurchase);

        purchaseDetail.setProduct(product);

        purchaseDetail.setQuantity(
                request.getQuantity());

        purchaseDetail.setUnitPrice(
                unitPrice);

        purchaseDetailRepository.save(
                purchaseDetail);

        product.setQuantity(
                product.getQuantity()
                        - request.getQuantity());

        productRepository.save(product);

        PurchaseItemResponse itemResponse = new PurchaseItemResponse(
                product.getProductId(),
                product.getProductName(),
                request.getQuantity(),
                unitPrice,
                totalAmount);

        return new PurchaseResponse(
                savedPurchase.getPurchaseId(),
                savedPurchase.getPurchaseDate(),
                savedPurchase.getTotalAmount(),
                List.of(itemResponse),
                "Purchase completed successfully");

    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<PurchaseResponse> getMyPurchases(
            String userEmail,
            String productName,
            LocalDate fromDate,
            LocalDate toDate,
            Pageable pageable) {

        User user = getUserByEmail(userEmail);

        validateDateRange(
                fromDate,
                toDate);

        LocalDateTime fromDateTime = fromDate == null
                ? null
                : fromDate.atStartOfDay();

        LocalDateTime toDateTime = toDate == null
                ? null
                : toDate
                        .plusDays(1)
                        .atStartOfDay()
                        .minusNanos(1);

        boolean productFilterPresent = productName != null
                && !productName.isBlank();

        boolean dateFilterPresent = fromDateTime != null
                && toDateTime != null;

        Page<Purchase> purchases;

        if (productFilterPresent
                && dateFilterPresent) {

            purchases = purchaseRepository
                    .findByUserProductNameAndDateRange(
                            user.getUserId(),
                            productName.trim(),
                            fromDateTime,
                            toDateTime,
                            pageable);

        } else if (productFilterPresent) {

            purchases = purchaseRepository
                    .findByUserAndProductName(
                            user.getUserId(),
                            productName.trim(),
                            pageable);

        } else if (dateFilterPresent) {

            purchases = purchaseRepository
                    .findByUser_UserIdAndPurchaseDateBetween(
                            user.getUserId(),
                            fromDateTime,
                            toDateTime,
                            pageable);

        } else {

            purchases = purchaseRepository
                    .findByUser_UserId(
                            user.getUserId(),
                            pageable);
        }

        Page<PurchaseResponse> responsePage = purchases.map(
                this::mapToPurchaseResponse);

        return PageResponse.from(responsePage);
    }

    @Override
    @Transactional(readOnly = true)
    public PurchaseResponse getMyPurchaseById(
            Long purchaseId,
            String userEmail) {

        User user = getUserByEmail(userEmail);

        Purchase purchase = purchaseRepository
                .findByPurchaseIdAndUser_UserId(
                        purchaseId,
                        user.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Purchase not found"));

        return mapToPurchaseResponse(purchase);
    }

    private User getUserByEmail(
            String userEmail) {

        String normalizedEmail = userEmail
                .trim()
                .toLowerCase();

        return userRepository
                .findByEmail(normalizedEmail)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Authenticated user not found"));
    }

    private PurchaseResponse mapToPurchaseResponse(
            Purchase purchase) {

        List<PurchaseDetail> purchaseDetails = purchaseDetailRepository
                .findByPurchase_PurchaseId(
                        purchase.getPurchaseId());

        List<PurchaseItemResponse> items = purchaseDetails
                .stream()
                .map(detail -> {

                    BigDecimal lineTotal = detail.getUnitPrice()
                            .multiply(
                                    BigDecimal.valueOf(
                                            detail.getQuantity()));

                    return new PurchaseItemResponse(
                            detail
                                    .getProduct()
                                    .getProductId(),

                            detail
                                    .getProduct()
                                    .getProductName(),

                            detail.getQuantity(),

                            detail.getUnitPrice(),

                            lineTotal);
                })
                .toList();

        return new PurchaseResponse(
                purchase.getPurchaseId(),
                purchase.getPurchaseDate(),
                purchase.getTotalAmount(),
                items,
                "Purchase details retrieved successfully");
    }

    private void validateDateRange(
            LocalDate fromDate,
            LocalDate toDate) {

        boolean onlyOneDateProvided = (fromDate == null
                && toDate != null)
                ||
                (fromDate != null
                        && toDate == null);

        if (onlyOneDateProvided) {

            throw new IllegalArgumentException(
                    "Both fromDate and toDate must be provided together");
        }

        if (fromDate != null
                && fromDate.isAfter(toDate)) {

            throw new IllegalArgumentException(
                    "fromDate cannot be after toDate");
        }
    }
}

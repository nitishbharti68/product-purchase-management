package com.nitish.product_purchase_management.repository;

import com.nitish.product_purchase_management.entity.Purchase;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;

import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PurchaseRepository
        extends JpaRepository<Purchase, Long> {

    Page<Purchase> findByUser_UserId(
            Long userId,
            Pageable pageable);

    Optional<Purchase> findByPurchaseIdAndUser_UserId(
            Long purchaseId,
            Long userId);

    Page<Purchase> findByUser_UserIdAndPurchaseDateBetween(
            Long userId,
            LocalDateTime fromDate,
            LocalDateTime toDate,
            Pageable pageable);

    @Query(value = """
            SELECT DISTINCT p
            FROM Purchase p
            JOIN p.purchaseDetails pd
            JOIN pd.product product
            WHERE p.user.userId = :userId
            AND LOWER(product.productName)
                LIKE LOWER(
                    CONCAT('%', :productName, '%')
                )
            """,

            countQuery = """
                    SELECT COUNT(DISTINCT p)
                    FROM Purchase p
                    JOIN p.purchaseDetails pd
                    JOIN pd.product product
                    WHERE p.user.userId = :userId
                    AND LOWER(product.productName)
                        LIKE LOWER(
                            CONCAT('%', :productName, '%')
                        )
                    """)
    Page<Purchase> findByUserAndProductName(
            @Param("userId") Long userId,

            @Param("productName") String productName,

            Pageable pageable);

    @Query(value = """
            SELECT DISTINCT p
            FROM Purchase p
            JOIN p.purchaseDetails pd
            JOIN pd.product product
            WHERE p.user.userId = :userId
            AND LOWER(product.productName)
                LIKE LOWER(
                    CONCAT('%', :productName, '%')
                )
            AND p.purchaseDate
                BETWEEN :fromDate AND :toDate
            """,

            countQuery = """
                    SELECT COUNT(DISTINCT p)
                    FROM Purchase p
                    JOIN p.purchaseDetails pd
                    JOIN pd.product product
                    WHERE p.user.userId = :userId
                    AND LOWER(product.productName)
                        LIKE LOWER(
                            CONCAT('%', :productName, '%')
                        )
                    AND p.purchaseDate
                        BETWEEN :fromDate AND :toDate
                    """)
    Page<Purchase> findByUserProductNameAndDateRange(
            @Param("userId") Long userId,

            @Param("productName") String productName,

            @Param("fromDate") LocalDateTime fromDate,

            @Param("toDate") LocalDateTime toDate,

            Pageable pageable);
}
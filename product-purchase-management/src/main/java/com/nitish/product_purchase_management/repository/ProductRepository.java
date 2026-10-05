package com.nitish.product_purchase_management.repository;

import com.nitish.product_purchase_management.entity.Product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository
        extends JpaRepository<Product, Long> {

    Page<Product> findByActiveTrue(
            Pageable pageable);

    Page<Product> findByActiveTrueAndProductNameContainingIgnoreCase(
            String productName,
            Pageable pageable);

    Optional<Product> findByProductIdAndActiveTrue(
            Long productId);

    Page<Product> findByProductNameContainingIgnoreCase(
            String productName,
            Pageable pageable);
}
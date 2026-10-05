package com.nitish.product_purchase_management.service.impl;

import com.nitish.product_purchase_management.dto.request.ProductRequest;
import com.nitish.product_purchase_management.dto.response.PageResponse;
import com.nitish.product_purchase_management.dto.response.ProductResponse;
import com.nitish.product_purchase_management.entity.Product;
import com.nitish.product_purchase_management.entity.User;
import com.nitish.product_purchase_management.exception.ResourceNotFoundException;
import com.nitish.product_purchase_management.repository.ProductRepository;
import com.nitish.product_purchase_management.repository.UserRepository;
import com.nitish.product_purchase_management.service.ProductService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductServiceImpl
                implements ProductService {

        private final ProductRepository productRepository;

        private final UserRepository userRepository;

        public ProductServiceImpl(
                        ProductRepository productRepository,
                        UserRepository userRepository) {
                this.productRepository = productRepository;

                this.userRepository = userRepository;
        }

        @Override
        @Transactional
        @PreAuthorize("hasRole('ADMIN')")
        public ProductResponse addProduct(
                        ProductRequest request,
                        String adminEmail) {
                User admin = userRepository
                                .findByEmail(
                                                adminEmail
                                                                .trim()
                                                                .toLowerCase())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Admin user not found"));

                Product product = new Product();

                product.setProductName(
                                request.getProductName().trim());

                product.setDescription(
                                request.getDescription().trim());

                product.setPrice(
                                request.getPrice());

                product.setQuantity(
                                request.getQuantity());

                product.setCategory(
                                normalizeOptionalText(
                                                request.getCategory()));

                product.setImagePath(
                                normalizeOptionalText(
                                                request.getImagePath()));

                product.setActive(true);
                product.setCreatedBy(admin);

                Product savedProduct = productRepository.save(product);

                return mapToResponse(savedProduct);
        }

        @Override
        @Transactional(readOnly = true)
        public PageResponse<ProductResponse> getActiveProducts(
                        String search,
                        Pageable pageable) {
                Page<Product> products;

                if (search == null
                                || search.isBlank()) {

                        products = productRepository
                                        .findByActiveTrue(
                                                        pageable);

                } else {

                        products = productRepository
                                        .findByActiveTrueAndProductNameContainingIgnoreCase(
                                                        search.trim(),
                                                        pageable);
                }

                Page<ProductResponse> responsePage = products.map(
                                this::mapToResponse);

                return PageResponse.from(
                                responsePage);
        }

        @Override
        @Transactional(readOnly = true)
        public ProductResponse getActiveProduct(
                        Long productId) {
                Product product = productRepository
                                .findByProductIdAndActiveTrue(
                                                productId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Product not found"));

                return mapToResponse(product);
        }

        @Override
        @Transactional(readOnly = true)
        @PreAuthorize("hasRole('ADMIN')")
        public PageResponse<ProductResponse> getAllProductsForAdmin(
                        String search,
                        Pageable pageable) {
                Page<Product> products;

                if (search == null
                                || search.isBlank()) {

                        products = productRepository
                                        .findAll(pageable);

                } else {

                        products = productRepository
                                        .findByProductNameContainingIgnoreCase(
                                                        search.trim(),
                                                        pageable);
                }

                Page<ProductResponse> responsePage = products.map(
                                this::mapToResponse);

                return PageResponse.from(
                                responsePage);
        }

        private ProductResponse mapToResponse(
                        Product product) {
                return new ProductResponse(
                                product.getProductId(),
                                product.getProductName(),
                                product.getDescription(),
                                product.getPrice(),
                                product.getQuantity(),
                                product.getCategory(),
                                product.getImagePath(),
                                product.getActive(),
                                product.getCreatedDate());
        }

        private String normalizeOptionalText(
                        String value) {
                if (value == null
                                || value.isBlank()) {
                        return null;
                }

                return value.trim();
        }

        @Override
        @Transactional
        @PreAuthorize("hasRole('ADMIN')")
        public ProductResponse updateProduct(
                        Long productId,
                        ProductRequest request) {

                Product product = productRepository
                                .findById(productId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Product not found"));

                product.setProductName(
                                request.getProductName().trim());

                product.setDescription(
                                request.getDescription().trim());

                product.setPrice(
                                request.getPrice());

                product.setQuantity(
                                request.getQuantity());

                product.setCategory(
                                normalizeOptionalText(
                                                request.getCategory()));

                product.setImagePath(
                                normalizeOptionalText(
                                                request.getImagePath()));

                /*
                 * createdBy and createdDate are never changed.
                 * They record who originally created the product.
                 */

                Product savedProduct = productRepository.save(product);

                return mapToResponse(savedProduct);
        }

        @Override
        @Transactional
        @PreAuthorize("hasRole('ADMIN')")
        public ProductResponse updateProductStatus(
                        Long productId,
                        Boolean active) {

                Product product = productRepository
                                .findById(productId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Product not found"));

                product.setActive(active);

                Product savedProduct = productRepository.save(product);

                return mapToResponse(savedProduct);
        }
}
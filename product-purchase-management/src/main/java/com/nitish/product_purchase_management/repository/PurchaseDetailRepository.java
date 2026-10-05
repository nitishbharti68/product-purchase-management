package com.nitish.product_purchase_management.repository;

import com.nitish.product_purchase_management.entity.PurchaseDetail;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseDetailRepository
        extends JpaRepository<PurchaseDetail, Long> {

    List<PurchaseDetail> findByPurchase_PurchaseId(
            Long purchaseId);
}

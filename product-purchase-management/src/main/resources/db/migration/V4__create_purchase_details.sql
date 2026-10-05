CREATE TABLE purchase_details (
    purchase_detail_id BIGSERIAL PRIMARY KEY,

    purchase_id BIGINT NOT NULL,

    product_id BIGINT NOT NULL,

    quantity INTEGER NOT NULL,

    unit_price DECIMAL(19, 2) NOT NULL,

    CONSTRAINT chk_purchase_details_quantity
        CHECK (quantity > 0),

    CONSTRAINT chk_purchase_details_unit_price
        CHECK (unit_price > 0),

    CONSTRAINT fk_purchase_details_purchase
        FOREIGN KEY (purchase_id)
        REFERENCES purchases(purchase_id)
        ON DELETE CASCADE,

    CONSTRAINT fk_purchase_details_product
        FOREIGN KEY (product_id)
        REFERENCES products(product_id)
        ON DELETE RESTRICT
);
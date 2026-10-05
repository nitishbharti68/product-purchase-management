CREATE INDEX idx_products_product_name
    ON products(product_name);

CREATE INDEX idx_products_active
    ON products(active);

CREATE INDEX idx_purchases_user_id
    ON purchases(user_id);

CREATE INDEX idx_purchases_purchase_date
    ON purchases(purchase_date);

CREATE INDEX idx_purchases_user_date
    ON purchases(user_id, purchase_date);

CREATE INDEX idx_purchase_details_purchase_id
    ON purchase_details(purchase_id);

CREATE INDEX idx_purchase_details_product_id
    ON purchase_details(product_id);
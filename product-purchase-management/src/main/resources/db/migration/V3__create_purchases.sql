CREATE TABLE purchases (
    purchase_id BIGSERIAL PRIMARY KEY,

    user_id BIGINT NOT NULL,

    purchase_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    total_amount DECIMAL(19, 2) NOT NULL,

    CONSTRAINT chk_purchases_total_amount
        CHECK (total_amount > 0),

    CONSTRAINT fk_purchases_user
        FOREIGN KEY (user_id)
        REFERENCES users(user_id)
        ON DELETE RESTRICT
);
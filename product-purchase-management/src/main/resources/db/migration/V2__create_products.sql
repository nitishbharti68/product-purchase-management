CREATE TABLE products (
    product_id BIGSERIAL PRIMARY KEY,

    product_name VARCHAR(150) NOT NULL,

    description VARCHAR(1000) NOT NULL,

    price DECIMAL(19, 2) NOT NULL,

    quantity INTEGER NOT NULL,

    category VARCHAR(100),

    image_path VARCHAR(500),

    active BOOLEAN NOT NULL DEFAULT TRUE,

    created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    created_by BIGINT NOT NULL,

    version BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT chk_products_price
        CHECK (price > 0),

    CONSTRAINT chk_products_quantity
        CHECK (quantity >= 0),

    CONSTRAINT fk_products_created_by
        FOREIGN KEY (created_by)
        REFERENCES users(user_id)
        ON DELETE RESTRICT
);
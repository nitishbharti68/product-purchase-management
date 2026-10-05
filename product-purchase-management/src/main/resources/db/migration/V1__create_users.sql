CREATE TABLE users (
    user_id BIGSERIAL PRIMARY KEY,

    first_name VARCHAR(100) NOT NULL,

    last_name VARCHAR(100) NOT NULL,

    email VARCHAR(255) NOT NULL UNIQUE,

    password_hash VARCHAR(255) NOT NULL,

    role VARCHAR(20) NOT NULL,

    created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    last_login TIMESTAMP
);
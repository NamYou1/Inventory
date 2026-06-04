-- V2__Add_customers_table.sql
-- Add customers table for customer management

CREATE TABLE IF NOT EXISTS customers (
    id BIGSERIAL PRIMARY KEY,
    customer_code VARCHAR(100) UNIQUE NOT NULL,
    full_name VARCHAR(255),
    gender VARCHAR(20),
    birth_date DATE,
    phone VARCHAR(20),
    email VARCHAR(255),
    address VARCHAR(500),
    city VARCHAR(100),
    country VARCHAR(100),
    customer_type VARCHAR(50),
    status VARCHAR(50),
    credit_limit NUMERIC(25, 4),
    current_debt NUMERIC(25, 4),
    reward_point INTEGER,
    note TEXT,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- Create indices as specified in the entity
CREATE INDEX IF NOT EXISTS idx_customer_code ON customers(customer_code);
CREATE INDEX IF NOT EXISTS idx_customer_phone ON customers(phone);
CREATE INDEX IF NOT EXISTS idx_customer_email ON customers(email);
CREATE INDEX IF NOT EXISTS idx_customer_status ON customers(status);
CREATE INDEX IF NOT EXISTS idx_customer_created_at ON customers(created_at);


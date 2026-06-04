-- V3__Add_remaining_tables.sql
-- Add all remaining core tables for the system

-- =============================================
-- USERS AND AUTHENTICATION
-- =============================================
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    is_locked BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    deleted_by VARCHAR(100)
);

CREATE INDEX IF NOT EXISTS idx_users_active ON users(is_active, is_locked, deleted_at);
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);

CREATE TABLE IF NOT EXISTS user_profiles (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    full_name VARCHAR(255),
    phone_number VARCHAR(20),
    profile_image VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT fk_user_profile_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS roles (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(64) UNIQUE NOT NULL,
    name VARCHAR(120) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

CREATE INDEX IF NOT EXISTS idx_role_code ON roles(code);

CREATE TABLE IF NOT EXISTS permission_groups (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS permissions (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(100) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    permission_group_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    CONSTRAINT fk_permission_group FOREIGN KEY (permission_group_id) REFERENCES permission_groups(id) ON DELETE SET NULL
);

CREATE INDEX IF NOT EXISTS idx_permission_code ON permissions(code);

CREATE TABLE IF NOT EXISTS refresh_tokens (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    token VARCHAR(500) NOT NULL,
    expiry_date TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_refresh_token_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_refresh_tokens_user_id ON refresh_tokens(user_id);

CREATE TABLE IF NOT EXISTS verification_tokens (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    token VARCHAR(500) NOT NULL,
    token_type VARCHAR(50),
    expiry_date TIMESTAMP NOT NULL,
    used BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_verification_token_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- =============================================
-- CATEGORY AND PRODUCT RELATED
-- =============================================
CREATE TABLE IF NOT EXISTS tbl_category (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    description TEXT,
    status VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    is_deleted BOOLEAN DEFAULT FALSE,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    deleted_by VARCHAR(100),
    deleted_at TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_category_status ON tbl_category(status);

CREATE TABLE IF NOT EXISTS tbl_sub_category (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    category_id BIGINT NOT NULL,
    description TEXT,
    status VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    is_deleted BOOLEAN DEFAULT FALSE,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    deleted_by VARCHAR(100),
    deleted_at TIMESTAMP,
    CONSTRAINT fk_sub_category_category FOREIGN KEY (category_id) REFERENCES tbl_category(id)
);

CREATE TABLE IF NOT EXISTS tbl_unit (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    is_deleted BOOLEAN DEFAULT FALSE,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    deleted_by VARCHAR(100),
    deleted_at TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_unit_code ON tbl_unit(code);

-- =============================================
-- SALES AND BUSINESS ENTITIES
-- =============================================
CREATE TABLE IF NOT EXISTS tbl_seller (
    id BIGSERIAL PRIMARY KEY,
    seller_code VARCHAR(100) UNIQUE,
    name VARCHAR(255),
    phone VARCHAR(20),
    email VARCHAR(255),
    address VARCHAR(500),
    city VARCHAR(100),
    country VARCHAR(100),
    status VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    is_deleted BOOLEAN DEFAULT FALSE,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    deleted_by VARCHAR(100),
    deleted_at TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_seller_email ON tbl_seller(email);
CREATE INDEX IF NOT EXISTS idx_seller_phone ON tbl_seller(phone);
CREATE INDEX IF NOT EXISTS idx_seller_status ON tbl_seller(status);

CREATE TABLE IF NOT EXISTS tbl_suppliers (
    id BIGSERIAL PRIMARY KEY,
    supplier_code VARCHAR(100) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    email VARCHAR(255),
    address VARCHAR(500),
    city VARCHAR(100),
    country VARCHAR(100),
    contact_person VARCHAR(255),
    payment_terms VARCHAR(100),
    status VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    is_deleted BOOLEAN DEFAULT FALSE,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    deleted_by VARCHAR(100),
    deleted_at TIMESTAMP
);

-- =============================================
-- INVOICES AND PAYMENTS
-- =============================================
CREATE TABLE IF NOT EXISTS invoices (
    id BIGSERIAL PRIMARY KEY,
    invoice_no VARCHAR(100) UNIQUE NOT NULL,
    invoice_date TIMESTAMP,
    customer_id BIGINT,
    customer_name VARCHAR(255),
    total_amount NUMERIC(19, 4),
    paid_amount NUMERIC(19, 4),
    remaining_amount NUMERIC(19, 4),
    status VARCHAR(50),
    payment_status VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    is_deleted BOOLEAN DEFAULT FALSE,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    deleted_by VARCHAR(100),
    deleted_at TIMESTAMP,
    CONSTRAINT fk_invoice_customer FOREIGN KEY (customer_id) REFERENCES customers(id)
);

CREATE INDEX IF NOT EXISTS idx_invoice_status ON invoices(status);

CREATE TABLE IF NOT EXISTS payments (
    id BIGSERIAL PRIMARY KEY,
    payment_no VARCHAR(100) UNIQUE NOT NULL,
    invoice_id BIGINT,
    payment_date TIMESTAMP,
    amount NUMERIC(19, 4),
    payment_method VARCHAR(50),
    reference_no VARCHAR(100),
    status VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    is_deleted BOOLEAN DEFAULT FALSE,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    deleted_by VARCHAR(100),
    deleted_at TIMESTAMP,
    CONSTRAINT fk_payment_invoice FOREIGN KEY (invoice_id) REFERENCES invoices(id)
);

CREATE INDEX IF NOT EXISTS idx_payment_status ON payments(status);

-- =============================================
-- SALES
-- =============================================
CREATE TABLE IF NOT EXISTS sales (
    id BIGSERIAL PRIMARY KEY,
    sale_no VARCHAR(100) UNIQUE NOT NULL,
    sale_date TIMESTAMP,
    customer_id BIGINT,
    store_id BIGINT,
    total_amount NUMERIC(19, 4),
    discount_amount NUMERIC(19, 4),
    grand_total NUMERIC(19, 4),
    payment_status VARCHAR(50),
    sale_status VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    is_deleted BOOLEAN DEFAULT FALSE,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    deleted_by VARCHAR(100),
    deleted_at TIMESTAMP,
    CONSTRAINT fk_sale_customer FOREIGN KEY (customer_id) REFERENCES customers(id),
    CONSTRAINT fk_sale_store FOREIGN KEY (store_id) REFERENCES tbl_stores(id)
);

CREATE INDEX IF NOT EXISTS idx_sale_status ON sales(sale_status);

CREATE TABLE IF NOT EXISTS sale_items (
    id BIGSERIAL PRIMARY KEY,
    sale_id BIGINT NOT NULL,
    product_id BIGINT,
    quantity NUMERIC(19, 4),
    unit_price NUMERIC(19, 4),
    discount NUMERIC(19, 4),
    subtotal NUMERIC(19, 4),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    is_deleted BOOLEAN DEFAULT FALSE,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    deleted_by VARCHAR(100),
    deleted_at TIMESTAMP,
    CONSTRAINT fk_sale_item_sale FOREIGN KEY (sale_id) REFERENCES sales(id) ON DELETE CASCADE,
    CONSTRAINT fk_sale_item_product FOREIGN KEY (product_id) REFERENCES tbl_product(id)
);

CREATE INDEX IF NOT EXISTS idx_sale_item_sale_id ON sale_items(sale_id);

-- =============================================
-- STOCK ADJUSTMENTS AND TRANSACTIONS
-- =============================================
CREATE TABLE IF NOT EXISTS stock_adjustments (
    id BIGSERIAL PRIMARY KEY,
    adjustment_no VARCHAR(100) UNIQUE NOT NULL,
    reference_no VARCHAR(100),
    product_id BIGINT NOT NULL,
    store_id BIGINT,
    old_quantity NUMERIC(19, 4),
    new_quantity NUMERIC(19, 4),
    adjustment_date TIMESTAMP,
    adjustment_type VARCHAR(50),
    reason VARCHAR(255),
    status VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    is_deleted BOOLEAN DEFAULT FALSE,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    deleted_by VARCHAR(100),
    deleted_at TIMESTAMP,
    CONSTRAINT fk_stock_adj_product FOREIGN KEY (product_id) REFERENCES tbl_product(id)
);

CREATE INDEX IF NOT EXISTS idx_adj_reference_no ON stock_adjustments(reference_no);
CREATE INDEX IF NOT EXISTS idx_adj_date ON stock_adjustments(adjustment_date);
CREATE INDEX IF NOT EXISTS idx_adj_status ON stock_adjustments(status);
CREATE INDEX IF NOT EXISTS idx_adj_type ON stock_adjustments(adjustment_type);
CREATE INDEX IF NOT EXISTS idx_adj_product ON stock_adjustments(product_id);

CREATE TABLE IF NOT EXISTS tbl_transaction (
    id BIGSERIAL PRIMARY KEY,
    transaction_no VARCHAR(100) UNIQUE NOT NULL,
    transaction_type VARCHAR(50),
    reference_id BIGINT,
    reference_type VARCHAR(50),
    store_id BIGINT,
    amount NUMERIC(19, 4),
    description TEXT,
    transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    is_deleted BOOLEAN DEFAULT FALSE,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    deleted_by VARCHAR(100),
    deleted_at TIMESTAMP,
    CONSTRAINT fk_transaction_store FOREIGN KEY (store_id) REFERENCES tbl_stores(id)
);

CREATE INDEX IF NOT EXISTS idx_transaction_type ON tbl_transaction(transaction_type);
CREATE INDEX IF NOT EXISTS idx_transaction_status ON tbl_transaction(status);

-- =============================================
-- AUDIT LOGGING
-- =============================================
CREATE TABLE IF NOT EXISTS tbl_audit_log (
    id BIGSERIAL PRIMARY KEY,
    entity_type VARCHAR(100),
    entity_id BIGINT,
    action VARCHAR(50),
    old_values TEXT,
    new_values TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100)
);

CREATE INDEX IF NOT EXISTS idx_audit_entity ON tbl_audit_log(entity_type, entity_id);


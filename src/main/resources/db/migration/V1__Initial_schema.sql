-- V1__Initial_schema.sql
-- Initial database schema for Inventory Management System

-- =============================================
-- STORES TABLE
-- =============================================
CREATE TABLE IF NOT EXISTS tbl_stores (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    code VARCHAR(100),
    address VARCHAR(500),
    phone VARCHAR(20),
    email VARCHAR(255),
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

CREATE INDEX idx_store_name ON tbl_stores(name);
CREATE INDEX idx_store_code ON tbl_stores(code);
CREATE INDEX idx_store_email ON tbl_stores(email);
CREATE INDEX idx_store_phone ON tbl_stores(phone);
CREATE INDEX idx_store_status ON tbl_stores(status);
CREATE INDEX idx_store_city_country ON tbl_stores(city, country);

-- =============================================
-- PRODUCTS TABLE
-- =============================================
CREATE TABLE IF NOT EXISTS tbl_product (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(100) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    category VARCHAR(100),
    unit_price NUMERIC(19, 4),
    cost_price NUMERIC(19, 4),
    quantity INTEGER DEFAULT 0,
    reorder_level INTEGER DEFAULT 10,
    status VARCHAR(50) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    is_deleted BOOLEAN DEFAULT FALSE,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    deleted_by VARCHAR(100),
    deleted_at TIMESTAMP
);

-- =============================================
-- STOCK TABLE
-- =============================================
CREATE TABLE IF NOT EXISTS tbl_stock (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL,
    store_id BIGINT NOT NULL,
    quantity NUMERIC(19, 4) DEFAULT 0,
    last_restock_date TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    is_deleted BOOLEAN DEFAULT FALSE,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    deleted_by VARCHAR(100),
    deleted_at TIMESTAMP,
    CONSTRAINT fk_stock_product FOREIGN KEY (product_id) REFERENCES tbl_product(id),
    CONSTRAINT fk_stock_store FOREIGN KEY (store_id) REFERENCES tbl_stores(id),
    CONSTRAINT uk_stock_product_store UNIQUE (product_id, store_id)
);

CREATE INDEX idx_stock_product_id ON tbl_stock(product_id);
CREATE INDEX idx_stock_store_id ON tbl_stock(store_id);

-- =============================================
-- PURCHASES TABLE
-- =============================================
CREATE TABLE IF NOT EXISTS tbl_purchases (
    id BIGSERIAL PRIMARY KEY,
    no VARCHAR(100) UNIQUE NOT NULL,
    date TIMESTAMP,
    reference VARCHAR(255),
    supplier_id BIGINT,
    supplier_name VARCHAR(255),
    store_id BIGINT NOT NULL,
    store_name VARCHAR(255),
    seller_id BIGINT,
    seller_name VARCHAR(255),
    total NUMERIC(19, 4) DEFAULT 0,
    total_discount NUMERIC(19, 4) DEFAULT 0,
    grand_total NUMERIC(19, 4) DEFAULT 0,
    purchase_status VARCHAR(50) DEFAULT 'ORDERED',
    payment_status VARCHAR(50) DEFAULT 'PENDING',
    status VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    is_deleted BOOLEAN DEFAULT FALSE,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    deleted_by VARCHAR(100),
    deleted_at TIMESTAMP,
    CONSTRAINT fk_purchase_store FOREIGN KEY (store_id) REFERENCES tbl_stores(id)
);

CREATE INDEX idx_purchase_no ON tbl_purchases(no);
CREATE INDEX idx_purchase_date ON tbl_purchases(date);
CREATE INDEX idx_purchase_status ON tbl_purchases(purchase_status);
CREATE INDEX idx_purchase_payment_status ON tbl_purchases(payment_status);
CREATE INDEX idx_purchase_store ON tbl_purchases(store_id);

-- =============================================
-- PURCHASE_ITEMS TABLE (corrected name)
-- =============================================
CREATE TABLE IF NOT EXISTS table_purchase_items (
    id BIGSERIAL PRIMARY KEY,
    purchase_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    store_id BIGINT NOT NULL,
    quantity INTEGER DEFAULT 0,
    cost_price NUMERIC(19, 4),
    subtotal NUMERIC(19, 4) DEFAULT 0,
    total_discount NUMERIC(19, 4) DEFAULT 0,
    unit_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    is_deleted BOOLEAN DEFAULT FALSE,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    deleted_by VARCHAR(100),
    deleted_at TIMESTAMP,
    CONSTRAINT fk_purchase_item_purchase FOREIGN KEY (purchase_id) REFERENCES tbl_purchases(id) ON DELETE CASCADE,
    CONSTRAINT fk_purchase_item_product FOREIGN KEY (product_id) REFERENCES tbl_product(id),
    CONSTRAINT fk_purchase_item_store FOREIGN KEY (store_id) REFERENCES tbl_stores(id)
);

CREATE INDEX idx_purchase_item_purchase ON table_purchase_items(purchase_id);
CREATE INDEX idx_purchase_item_product ON table_purchase_items(product_id);
CREATE INDEX idx_purchase_item_unit ON table_purchase_items(unit_id);

-- =============================================
-- TRANSFERS TABLE
-- =============================================
CREATE TABLE IF NOT EXISTS tbl_transfer (
    id BIGSERIAL PRIMARY KEY,
    transfer_no VARCHAR(100) UNIQUE NOT NULL,
    from_store_id BIGINT NOT NULL,
    to_store_id BIGINT NOT NULL,
    status VARCHAR(50) DEFAULT 'PENDING',
    total NUMERIC(19, 4) DEFAULT 0,
    shipping NUMERIC(19, 4) DEFAULT 0,
    grand_total NUMERIC(19, 4) DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    is_deleted BOOLEAN DEFAULT FALSE,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    deleted_by VARCHAR(100),
    deleted_at TIMESTAMP,
    CONSTRAINT fk_transfer_from_store FOREIGN KEY (from_store_id) REFERENCES tbl_stores(id),
    CONSTRAINT fk_transfer_to_store FOREIGN KEY (to_store_id) REFERENCES tbl_stores(id)
);

CREATE INDEX idx_transfer_from_store_id ON tbl_transfer(from_store_id);
CREATE INDEX idx_transfer_to_store_id ON tbl_transfer(to_store_id);
CREATE INDEX idx_transfer_status ON tbl_transfer(status);

-- =============================================
-- TRANSFER_ITEMS TABLE (corrected name)
-- =============================================
CREATE TABLE IF NOT EXISTS tbl_transfer_items (
    id BIGSERIAL PRIMARY KEY,
    transfer_id BIGINT NOT NULL,
    product_id INTEGER,
    quantity NUMERIC(19, 4) DEFAULT 0,
    unit_price NUMERIC(19, 4),
    subtotal NUMERIC(19, 4) DEFAULT 0,
    unit_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    is_deleted BOOLEAN DEFAULT FALSE,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    deleted_by VARCHAR(100),
    deleted_at TIMESTAMP,
    CONSTRAINT fk_transfer_item_transfer FOREIGN KEY (transfer_id) REFERENCES tbl_transfer(id) ON DELETE CASCADE
);

CREATE INDEX idx_transfer_item_transfer ON tbl_transfer_items(transfer_id);
CREATE INDEX idx_transfer_item_product ON tbl_transfer_items(product_id);
CREATE INDEX idx_transfer_item_unit ON tbl_transfer_items(unit_id);

-- =============================================
-- ADJUSTMENTS TABLE
-- =============================================
CREATE TABLE IF NOT EXISTS tbl_adjustment (
    id BIGSERIAL PRIMARY KEY,
    adjustment_no VARCHAR(100) UNIQUE NOT NULL,
    store_id BIGINT NOT NULL,
    adjustment_date TIMESTAMP,
    status VARCHAR(50) DEFAULT 'DRAFT',
    reason VARCHAR(500),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    is_deleted BOOLEAN DEFAULT FALSE,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    deleted_by VARCHAR(100),
    deleted_at TIMESTAMP,
    CONSTRAINT fk_adjustment_store FOREIGN KEY (store_id) REFERENCES tbl_stores(id)
);

CREATE INDEX idx_adjustment_store_id ON tbl_adjustment(store_id);
CREATE INDEX idx_adjustment_status ON tbl_adjustment(status);

-- =============================================
-- ADJUSTMENT_ITEMS TABLE
-- =============================================
CREATE TABLE IF NOT EXISTS tbl_adjustment_item (
    id BIGSERIAL PRIMARY KEY,
    adjustment_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity_before INTEGER,
    quantity_after INTEGER,
    difference INTEGER,
    reason VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    is_deleted BOOLEAN DEFAULT FALSE,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    deleted_by VARCHAR(100),
    deleted_at TIMESTAMP,
    CONSTRAINT fk_adjustment_item_adjustment FOREIGN KEY (adjustment_id) REFERENCES tbl_adjustment(id) ON DELETE CASCADE,
    CONSTRAINT fk_adjustment_item_product FOREIGN KEY (product_id) REFERENCES tbl_product(id)
);

CREATE INDEX idx_adjustment_item_adjustment_id ON tbl_adjustment_item(adjustment_id);
CREATE INDEX idx_adjustment_item_product_id ON tbl_adjustment_item(product_id);


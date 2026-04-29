package yoyo.inventory.services;

import yoyo.inventory.entities.Product;
import yoyo.inventory.entities.Stock;
import yoyo.inventory.entities.Stores;

import java.math.BigDecimal;

public interface StockService {

    // =====================================================
    // PURCHASE
    // =====================================================

    void purchaseIn(
            Stores store,
            Product product,
            BigDecimal quantity,
            BigDecimal costPrice
    );

    // =====================================================
    // SALE
    // =====================================================

    void saleOut(
            Stores store,
            Product product,
            BigDecimal quantity
    );

    // =====================================================
    // TRANSFER
    // =====================================================

    void transfer(
            Stores fromStore,
            Stores toStore,
            Product product,
            BigDecimal quantity
    );

    // =====================================================
    // ADJUSTMENT
    // =====================================================

    void adjustment(
            Stores store,
            Product product,
            BigDecimal quantity,
            boolean increase
    );

    // =====================================================
    // STOCK QUERY
    // =====================================================

    Stock getStock(
            Stores store,
            Product product
    );

    Stock getOrCreateStock(
            Stores store,
            Product product
    );

    // =====================================================
    // VALIDATION
    // =====================================================

    void validateStock(
            Stock stock,
            BigDecimal quantity
    );
}
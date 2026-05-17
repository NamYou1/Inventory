package yoyo.inventory.services;

import lombok.extern.java.Log;
import yoyo.inventory.dto.response.StockResponse;
import yoyo.inventory.entities.Product;
import yoyo.inventory.entities.Stock;
import yoyo.inventory.entities.Stores;
import yoyo.inventory.enums.AdjustmentType;


import java.math.BigDecimal;
public interface StockService {

    // =========================================
    // FIND STOCK
    // =========================================
    Stock findProductAndStoreById(Long productId  , Long storeId );
    StockResponse getById(Long id);
    StockResponse getByProductAndStore(Long productId, Long storeId);
//    Page<StockResponse> getAll(
//            StockFilterRequest filter,
//            Pageable pageable
//    );
    // =========================================
    // STOCK IN
    // =========================================
    void increaseStock(Long  productId, Long storeId, BigDecimal quantity, BigDecimal costPrice);
    // =========================================
    // STOCK OUT
    // =========================================
    void decreaseStock(Long productId, Long storeid, BigDecimal quantity);
    // =========================================
    // TRANSFER
    // =========================================
    void transferStock(long productId, Long fromStore, long toStore, BigDecimal quantity);
    // =========================================
    // ADJUSTMENT
    // =========================================
    void adjustStock(Long product, Long store, BigDecimal quantity, AdjustmentType type);

//    void decreaseStock(Product product, Stores store, BigDecimal quantity);
//
//    void transferStock(Long productId, long fromStore, long toStore, BigDecimal quantity);
//
//    void adjustStock(Log product, Long store, BigDecimal quantity, AdjustmentType type);

    // =========================================
    // VALIDATION
    // =========================================
    void validateStock(Stock stock, BigDecimal quantity);
}
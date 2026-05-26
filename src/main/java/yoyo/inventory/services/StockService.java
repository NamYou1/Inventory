package yoyo.inventory.services;

import lombok.extern.java.Log;
import yoyo.inventory.dto.response.StockResponse;
import yoyo.inventory.entities.Product;
import yoyo.inventory.entities.Stock;
import yoyo.inventory.entities.Stores;
import yoyo.inventory.enums.AdjustmentType;


import java.math.BigDecimal;
import org.springframework.data.domain.Page;
import java.util.Map;

public interface StockService {

    // =========================================
    // FIND STOCK
    // =========================================
    Stock findProductAndStoreById(Long productId  , Long storeId );
    StockResponse getById(Long id);
    StockResponse getByProductAndStore(Long productId, Long storeId);
    Page<StockResponse> getAll(Map<String, String> params);
    Page<StockResponse> getByStore(Long storeId, Map<String,String> params);
void reverseStock(
        Long productId,
        Long fromStoreId,
        Long toStoreId,
        BigDecimal quantity
);
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


    // =========================================
    // VALIDATION
    // =========================================
    void validateStock(Stock stock, BigDecimal quantity);

//    Stock findStock(long productId, Long storeId);///
}
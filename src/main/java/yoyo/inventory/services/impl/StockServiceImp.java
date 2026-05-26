package yoyo.inventory.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import yoyo.inventory.common.PageUtil;
import yoyo.inventory.dto.response.StockResponse;
import yoyo.inventory.entities.Product;
import yoyo.inventory.entities.Stock;
import yoyo.inventory.entities.Stores;
import yoyo.inventory.entities.User;
import yoyo.inventory.enums.AdjustmentType;
import yoyo.inventory.execption.InsufficientStockException;
import yoyo.inventory.execption.ResourceNotFoundException;
import yoyo.inventory.mappers.StockMapper;
import yoyo.inventory.repository.StockRepository;
import yoyo.inventory.repository.UserRepository;
import yoyo.inventory.services.ProductService;
import yoyo.inventory.services.StockService;
import yoyo.inventory.services.StoreService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;


@RequiredArgsConstructor
@Service
public class StockServiceImp implements StockService {
    private  final StockRepository stockRepository ;
    private  final ProductService productService ;
    private  final StockMapper stockMapper;
    private  final StoreService storeService ;
    private  final UserRepository userRepository;


    @Override
//    @Cacheable(cacheNames = "stock-entity-by-product-store", key = "#productId + ':' + #storeId")
    public Stock findProductAndStoreById(Long productId, Long storeId) {
        return  stockRepository.findByTblProductIdAndTblStoreId(productId , storeId)
                .orElseThrow(()-> new ResourceNotFoundException("Stock not found for product id " , productId  , storeId));
    }

    @Override
//    @Cacheable(cacheNames = "stock-response-by-id", key = "#id")
    public StockResponse getById(Long id) {
        Stock stock = stockRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Stock", id));
        return stockMapper.toResponse(stock);
    }

    @Override
//    @Cacheable(cacheNames = "stock-response-by-product-store", key = "#productId + ':' + #storeId")
    public StockResponse getByProductAndStore(Long productId, Long storeId) {
        Stock stock = stockRepository.findByTblProductIdAndTblStoreId(productId, storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Stock not found for product id " , productId  , storeId));
        return  stockMapper.toResponse(stock);
    }

    @Override
    public Page<StockResponse> getAll(Map<String, String> params) {
        Pageable pageable = PageUtil.fromParams(params);
        return stockRepository.findAll(pageable).map(stockMapper::toResponse);
    }

    @Override
    public Page<StockResponse> getByStore(Long storeId, Map<String, String> params) {
        Pageable pageable = PageUtil.fromParams(params);
        return stockRepository.findByTblStoreId(storeId, pageable).map(stockMapper::toResponse);

    }

    @Override
//    @CacheEvict(cacheNames = {"stock-entity-by-product-store", "stock-response-by-id", "stock-response-by-product-store", "txn-summary"}, allEntries = true)
    public void reverseStock(Long productId, Long fromStoreId, Long toStoreId, BigDecimal quantity) {
        // RETURN STOCK TO SOURCE
        increaseStock(
                productId,
                fromStoreId,
                quantity,
                BigDecimal.ZERO
        );

        // REMOVE STOCK FROM DESTINATION
        decreaseStock(
                productId,
                toStoreId,
                quantity
        );
    }

    @Override
//    @CacheEvict(cacheNames = {"stock-entity-by-product-store", "stock-response-by-id", "stock-response-by-product-store", "txn-summary"}, allEntries = true)
    public void increaseStock(Long productId, Long storeId , BigDecimal quantity, BigDecimal costPrice) {
        Product product= productService.findById(productId);
        Stores stores = storeService.findById(storeId);
        Stock stock = stockRepository.findByTblProductIdAndTblStoreId(productId , storeId )
                .orElseGet(() -> {
                    Stock newStock = new Stock();
                    newStock.setTblProduct(product);
                    newStock.setTblStore(stores);
                    newStock.setQuantity(BigDecimal.ZERO);
                    return newStock;
                });
        stock.setQuantity(stock.getQuantity().add(quantity));
        stock.setLastRestockDate(LocalDateTime.now());
        stockRepository.save(stock);
    }

    @Override
//    @CacheEvict(cacheNames = {"stock-entity-by-product-store", "stock-response-by-id", "stock-response-by-product-store", "txn-summary"}, allEntries = true)
    public void decreaseStock(Long productId, Long storeId, BigDecimal quantity) {
        Stock stock = findProductAndStoreById(productId, storeId);
        validateStock(stock, quantity);
        stock.setQuantity(stock.getQuantity().subtract(quantity));
        stockRepository.save(stock);
    }


    @Override
//    @CacheEvict(cacheNames = {"stock-entity-by-product-store", "stock-response-by-id", "stock-response-by-product-store", "txn-summary"}, allEntries = true)
    public void transferStock(long productId, Long fromStore, long toStore, BigDecimal quantity) {
        decreaseStock(productId, fromStore, quantity);
//        // increase destination
        increaseStock(productId, toStore, quantity, BigDecimal.ZERO);
//         increaseStock(productId, toStore, quantity, BigDecimal.ZERO);
    }

    @Override
    public void adjustStock(Long product, Long store, BigDecimal quantity, AdjustmentType type) {
        if (type == null) {
            throw new IllegalArgumentException("Adjustment type is required");
        }
        if (type == AdjustmentType.INCREASE) {
            increaseStock(product, store, quantity, BigDecimal.ZERO);
        } else if (type == AdjustmentType.DECREASE) {
            decreaseStock(product, store, quantity);
        }
    }

    @Override
    public void validateStock(Stock stock, BigDecimal quantity) {
        // Check stock exists
        if (stock == null) {
            throw new ResourceNotFoundException("Stock not found");
        }
        // Check quantity null or invalid
        if (quantity == null || quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
        // Check available stock
        if (stock.getQuantity().compareTo(quantity) < 0) {
            throw new InsufficientStockException(stock.getTblProduct().getName(), quantity.intValue(), stock.getQuantity().intValue());
        }
    }

//    @Override
//    public Stock findStock(long productId, Long storeId) {
//        return stockRepository
//                .findByProductIdAndStoreId(productId, storeId)
//                .orElseThrow(() ->
//                        new ResourceNotFoundException("Stock not found" , productId , storeId));
//    }


}

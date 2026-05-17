package yoyo.inventory.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;
import yoyo.inventory.dto.response.StockResponse;
import yoyo.inventory.entities.Product;
import yoyo.inventory.entities.Stock;
import yoyo.inventory.entities.Stores;
import yoyo.inventory.enums.AdjustmentType;
import yoyo.inventory.execption.InsufficientStockException;
import yoyo.inventory.execption.ResourceNotFoundExecption;
import yoyo.inventory.mappers.StockMapper;
import yoyo.inventory.repository.StockRepository;
import yoyo.inventory.services.ProductService;
import yoyo.inventory.services.StockService;
import yoyo.inventory.services.StoreService;
import java.math.BigDecimal;
import java.time.LocalDateTime;


@RequiredArgsConstructor
@Service
public class StockServiceImp implements StockService {
    private  final StockRepository stockRepository ;
    private  final ProductService productService ;
    private  final StockMapper stockMapper;
    private  final StoreService storeService ;


    @Override
    public Stock findProductAndStoreById(Long productId, Long storeId) {
        return  stockRepository.findByTblProductIdAndTblStoreId(productId , storeId)
                .orElseThrow(()-> new ResourceNotFoundExecption("Stock not found for product id " , productId  , storeId));
    }

    @Override
    public StockResponse getById(Long id) {
        Stock stock = stockRepository.findById(id).orElseThrow(()->new ResourceNotFoundExecption("Stock", id));
        return stockMapper.toResponse(stock);
    }

    @Override
    public StockResponse getByProductAndStore(Long productId, Long storeId) {
        Stock stock = stockRepository.findByTblProductIdAndTblStoreId(productId, storeId)
                .orElseThrow(() -> new ResourceNotFoundExecption("Stock not found for product id " , productId  , storeId));
        return  stockMapper.toResponse(stock);
    }

    @Override
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
    public void decreaseStock(Long productId, Long storeid, BigDecimal quantity) {
        Stock stock = findProductAndStoreById(productId, storeid);
        if (stock.getQuantity().compareTo(quantity) < 0) {
            throw  new InsufficientStockException(stock.getTblProduct().getName(), stock.getQuantity().intValue(), quantity.intValue());
        }
        stock.setQuantity(stock.getQuantity().subtract(quantity));
         validateStock(stock , quantity );
         stockRepository.save(stock);
    }


    @Override
    public void transferStock(long productId, Long fromStore, long toStore, BigDecimal quantity) {
        decreaseStock(productId, fromStore, quantity);
//        // increase destination
        increaseStock(productId, toStore, quantity, BigDecimal.ZERO);
//         increaseStock(productId, toStore, quantity, BigDecimal.ZERO);
    }

    @Override
    public void adjustStock(Long product, Long store, BigDecimal quantity, AdjustmentType type) {

    }


//    @Override
//    public void increaseStock(Product product, Product store, BigDecimal quantity, BigDecimal costPrice) {
//        Product productId = productService.findById(product.getId());
//        Stores storeId = storeService.findById(store.getId());
//        Stock stock = stockRepository.findByTblProductIdAndTblStoreId(productId.getId() , storeId.getId() )
//                .orElseGet(() -> {
//                    Stock newStock = new Stock();
//                    newStock.setTblProduct(productId);
//                    newStock.setTblStore(storeId);
//                    newStock.setQuantity(BigDecimal.ZERO);
//                    return newStock;
//                });
//        stock.setQuantity(stock.getQuantity().add(quantity));
//
//        stock.setLastRestockDate(LocalDateTime.now());
//
//        stockRepository.save(stock);
//
//    }

//    @Override
//    public void decreaseStock(Product product, Stores store, BigDecimal quantity) {
//        Stock stock = findProductAndStoreById(product.getId(), store.getId());
//        if (stock.getQuantity().compareTo(quantity) < 0) {
//            throw  new InsufficientStockException(product.getName(), stock.getQuantity().intValue(), quantity.intValue());
//        }
//        validateStock(stock , quantity );
//        stockRepository.save(stock);
//    }

//    @Override
//    public void transferStock(Long productId, long fromStore, long toStore, BigDecimal quantity) {
//        // decrease source
//        decreaseStock(productId, fromStore, quantity);
//        // increase destination
//        increaseStock(productId, toStore, quantity, BigDecimal.ZERO);
//    }

//    @Override
//    public void adjustStock(Log product, Long store, BigDecimal quantity, AdjustmentType type)
//    {
//
//    }
    @Override
    public void validateStock(Stock stock, BigDecimal quantity) {
        if (stock == null) {
            throw new ResourceNotFoundExecption("Stock not found");
        }
        if (stock.getQuantity().compareTo(quantity) < 0) {
            throw new InsufficientStockException("Insufficient stock");
        }
    }
}

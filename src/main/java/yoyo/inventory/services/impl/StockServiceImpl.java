package yoyo.inventory.services.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import yoyo.inventory.entities.Product;
import yoyo.inventory.entities.Stock;
import yoyo.inventory.entities.Stores;
import yoyo.inventory.execption.ResourceNotFoundExecption;
import yoyo.inventory.repository.StockRepository;
import yoyo.inventory.services.ProductService;
import yoyo.inventory.services.StockService;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class StockServiceImpl implements StockService {

    private final StockRepository stockRepository;
    private final ProductService productService;

    @Override
    public void purchaseIn(Stores store, Product product, BigDecimal quantity, BigDecimal costPrice) {
        Stock stock = getOrCreateStock(store, product);
        stock.setQuantity(stock.getQuantity().add(quantity));
        stock.setCostPrice(costPrice);
        stock.setLastRestockDate(LocalDateTime.now());
        stockRepository.save(stock);
    }

    @Override
    public void saleOut(Stores store, Product product, BigDecimal quantity) {
        Stock stock = getStock(store, product);
        validateStock(stock, quantity);
        stock.setQuantity(stock.getQuantity().subtract(quantity));
        stockRepository.save(stock);
    }

    @Override
    public void transfer(Stores fromStore, Stores toStore, Product product, BigDecimal quantity) {
        // decrease from store
        Stock fromStock = getStock(fromStore, product);
        validateStock(fromStock, quantity);
        fromStock.setQuantity(fromStock.getQuantity().subtract(quantity));
        stockRepository.save(fromStock);
        // increase to store
        Stock toStock = getOrCreateStock(toStore, product);
        toStock.setQuantity(toStock.getQuantity().add(quantity));
        stockRepository.save(toStock);
    }

    @Override
    public void adjustment(Stores store, Product product, BigDecimal quantity, boolean increase) {
        Stock stock = getOrCreateStock(store, product);
        if (increase) {
            stock.setQuantity(stock.getQuantity().add(quantity));
        } else {
            validateStock(stock, quantity);
            stock.setQuantity(stock.getQuantity().subtract(quantity));
        }
        stockRepository.save(stock);
    }

    @Override
    public Stock getStock(Stores store, Product product) {
//        Product productId = productService.findById(product.getId());

        return stockRepository.findByTblProductIdAndTblStoreId(store.getId(), product.getId())
                .orElseThrow(() -> new ResourceNotFoundExecption("Stock not found" , store.getId())
                );
    }

    @Override
    public Stock getOrCreateStock(Stores store, Product product) {
        return stockRepository.findByTblProductIdAndTblStoreId(store.getId(), product.getId()).orElseGet(() -> {
                    Stock stock = new Stock();
                    stock.setTblStore(store);
                    stock.setTblProduct(product);
                    stock.setQuantity(BigDecimal.ZERO);
                    stock.setCostPrice(BigDecimal.ZERO);
                    stock.setReorderLevel(0);
                    stock.setAlertQuantity(0);
                    return stock;
                });}

    @Override
    public void validateStock(Stock stock, BigDecimal quantity) {
        if (stock.getQuantity().compareTo(quantity) < 0) {
            throw new RuntimeException("Insufficient stock");
        }
    }
}
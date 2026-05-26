package yoyo.inventory.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import yoyo.inventory.entities.Product;
import yoyo.inventory.entities.Stock;
import yoyo.inventory.entities.Stores;
import yoyo.inventory.enums.AdjustmentType;
import yoyo.inventory.execption.InsufficientStockException;
import yoyo.inventory.mappers.StockMapper;
import yoyo.inventory.repository.StockRepository;
import yoyo.inventory.services.impl.StockServiceImp;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockServiceImpTest {

    @Mock
    private StockRepository stockRepository;

    @Mock
    private ProductService productService;

    @Mock
    private StockMapper stockMapper;

    @Mock
    private StoreService storeService;

    @InjectMocks
    private StockServiceImp stockService;

    @Test
    void testDecreaseStock_WhenSufficientStock_ShouldDeductAndSave() {
        // Arrange
        Long productId = 1L;
        Long storeId = 2L;
        BigDecimal quantityToDecrease = BigDecimal.valueOf(3);

        Product product = new Product();
        product.setId(productId);
        product.setName("Test Product");

        Stock stock = new Stock();
        stock.setId(10L);
        stock.setTblProduct(product);
        stock.setQuantity(BigDecimal.valueOf(10));

        when(stockRepository.findByTblProductIdAndTblStoreId(productId, storeId)).thenReturn(Optional.of(stock));

        // Act
        stockService.decreaseStock(productId, storeId, quantityToDecrease);

        // Assert
        assertThat(stock.getQuantity()).isEqualByComparingTo(BigDecimal.valueOf(7));
        verify(stockRepository).save(stock);
    }

    @Test
    void testDecreaseStock_WhenInsufficientStock_ShouldThrowInsufficientStockException() {
        // Arrange
        Long productId = 1L;
        Long storeId = 2L;
        BigDecimal quantityToDecrease = BigDecimal.valueOf(15);

        Product product = new Product();
        product.setId(productId);
        product.setName("Test Product");

        Stock stock = new Stock();
        stock.setId(10L);
        stock.setTblProduct(product);
        stock.setQuantity(BigDecimal.valueOf(10));

        when(stockRepository.findByTblProductIdAndTblStoreId(productId, storeId)).thenReturn(Optional.of(stock));

        // Act & Assert
        assertThatThrownBy(() -> stockService.decreaseStock(productId, storeId, quantityToDecrease))
                .isInstanceOf(InsufficientStockException.class);
        verify(stockRepository, never()).save(any(Stock.class));
    }

    @Test
    void testIncreaseStock_WhenStockExists_ShouldAddQuantity() {
        // Arrange
        Long productId = 1L;
        Long storeId = 2L;
        BigDecimal quantityToIncrease = BigDecimal.valueOf(5);

        Product product = new Product();
        product.setId(productId);

        Stores store = new Stores();
        store.setId(storeId);

        Stock stock = new Stock();
        stock.setQuantity(BigDecimal.valueOf(10));

        when(productService.findById(productId)).thenReturn(product);
        when(storeService.findById(storeId)).thenReturn(store);
        when(stockRepository.findByTblProductIdAndTblStoreId(productId, storeId)).thenReturn(Optional.of(stock));

        // Act
        stockService.increaseStock(productId, storeId, quantityToIncrease, BigDecimal.ZERO);

        // Assert
        assertThat(stock.getQuantity()).isEqualByComparingTo(BigDecimal.valueOf(15));
        verify(stockRepository).save(stock);
    }

    @Test
    void testIncreaseStock_WhenStockDoesNotExist_ShouldCreateNewStock() {
        // Arrange
        Long productId = 1L;
        Long storeId = 2L;
        BigDecimal quantityToIncrease = BigDecimal.valueOf(5);

        Product product = new Product();
        product.setId(productId);

        Stores store = new Stores();
        store.setId(storeId);

        when(productService.findById(productId)).thenReturn(product);
        when(storeService.findById(storeId)).thenReturn(store);
        when(stockRepository.findByTblProductIdAndTblStoreId(productId, storeId)).thenReturn(Optional.empty());

        // Act
        stockService.increaseStock(productId, storeId, quantityToIncrease, BigDecimal.ZERO);

        // Assert
        verify(stockRepository).save(argThat(savedStock -> 
            savedStock.getTblProduct() == product &&
            savedStock.getTblStore() == store &&
            savedStock.getQuantity().compareTo(BigDecimal.valueOf(5)) == 0
        ));
    }

    @Test
    void testAdjustStock_WhenIncrease_ShouldIncrease() {
        // Arrange
        Long productId = 1L;
        Long storeId = 2L;
        BigDecimal quantity = BigDecimal.valueOf(5);

        Product product = new Product();
        product.setId(productId);
        Stores store = new Stores();
        store.setId(storeId);

        Stock stock = new Stock();
        stock.setQuantity(BigDecimal.valueOf(10));

        when(productService.findById(productId)).thenReturn(product);
        when(storeService.findById(storeId)).thenReturn(store);
        when(stockRepository.findByTblProductIdAndTblStoreId(productId, storeId)).thenReturn(Optional.of(stock));

        // Act
        stockService.adjustStock(productId, storeId, quantity, AdjustmentType.INCREASE);

        // Assert
        assertThat(stock.getQuantity()).isEqualByComparingTo(BigDecimal.valueOf(15));
        verify(stockRepository).save(stock);
    }

    @Test
    void testAdjustStock_WhenDecrease_ShouldDecrease() {
        // Arrange
        Long productId = 1L;
        Long storeId = 2L;
        BigDecimal quantity = BigDecimal.valueOf(5);

        Product product = new Product();
        product.setId(productId);
        product.setName("Test Product");

        Stock stock = new Stock();
        stock.setTblProduct(product);
        stock.setQuantity(BigDecimal.valueOf(10));

        when(stockRepository.findByTblProductIdAndTblStoreId(productId, storeId)).thenReturn(Optional.of(stock));

        // Act
        stockService.adjustStock(productId, storeId, quantity, AdjustmentType.DECREASE);

        // Assert
        assertThat(stock.getQuantity()).isEqualByComparingTo(BigDecimal.valueOf(5));
        verify(stockRepository).save(stock);
    }
}

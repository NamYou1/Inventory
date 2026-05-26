package yoyo.inventory.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import yoyo.inventory.entities.Product;
import yoyo.inventory.entities.Stock;
import yoyo.inventory.entities.Stores;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class StockRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private StockRepository stockRepository;

    @Test
    void testFindByTblProductIdAndTblStoreId_WhenExists_ShouldReturnStock() {
        // Arrange
        Product product = new Product();
        product.setCode("P100");
        product.setName("Test Product");
        product = entityManager.persist(product);

        Stores store = new Stores();
        store.setCode("S100");
        store.setName("Test Store");
        store.setEmail("teststore@yoyo.com");
        store = entityManager.persist(store);

        Stock stock = new Stock();
        stock.setTblProduct(product);
        stock.setTblStore(store);
        stock.setQuantity(BigDecimal.TEN);
        stock = entityManager.persist(stock);

        entityManager.flush();

        // Act
        Optional<Stock> found = stockRepository.findByTblProductIdAndTblStoreId(product.getId(), store.getId());

        // Assert
        assertThat(found).isPresent();
        assertThat(found.get().getQuantity()).isEqualByComparingTo(BigDecimal.TEN);
    }

    @Test
    void testFindLowStockProducts_ShouldReturnLowStock() {
        // Arrange
        Product product = new Product();
        product.setCode("P200");
        product.setName("Test Product 2");
        product = entityManager.persist(product);

        Stores store = new Stores();
        store.setCode("S200");
        store.setName("Test Store 2");
        store.setEmail("teststore2@yoyo.com");
        store = entityManager.persist(store);

        // Low stock (quantity 3 <= reorderLevel 5)
        Stock stock1 = new Stock();
        stock1.setTblProduct(product);
        stock1.setTblStore(store);
        stock1.setQuantity(BigDecimal.valueOf(3));
        stock1.setReorderLevel(5);
        entityManager.persist(stock1);

        // Normal stock (quantity 10 > reorderLevel 5)
        Stock stock2 = new Stock();
        stock2.setTblProduct(product);
        stock2.setTblStore(store);
        stock2.setQuantity(BigDecimal.valueOf(10));
        stock2.setReorderLevel(5);
        entityManager.persist(stock2);

        entityManager.flush();

        // Act
        List<Stock> lowStock = stockRepository.findLowStockProducts();

        // Assert
        assertThat(lowStock).hasSize(1);
        assertThat(lowStock.get(0).getQuantity()).isEqualByComparingTo(BigDecimal.valueOf(3));
    }
}

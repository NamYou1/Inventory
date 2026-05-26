package yoyo.inventory.entities;
import jakarta.persistence.*;
import lombok.*;
import yoyo.inventory.enums.SaleStatus;

import java.math.BigDecimal;

@Entity
@Table(name = "sale_items", indexes = {
        @Index(name = "idx_sale_item_sale", columnList = "sale_id"),
        @Index(name = "idx_sale_item_product", columnList = "product_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaleItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // =====================================
    // RELATIONSHIP
    // =====================================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sale_id")
    private Sale sale;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    // =====================================
    // SALE DETAIL
    // =====================================

    @Column(precision = 15, scale = 4)
    private BigDecimal quantity;

    @Column(precision = 25, scale = 4)
    private BigDecimal unitPrice;

    @Column(precision = 25, scale = 4)
    private BigDecimal discountAmount;

    @Column(precision = 25, scale = 4)
    private BigDecimal totalPrice;
}

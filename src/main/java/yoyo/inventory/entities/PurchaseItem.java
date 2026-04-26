package yoyo.inventory.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "table_purchase_items")
public class PurchaseItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "purchase_id", nullable = false)
    private Purchases purchase;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false, precision = 15, scale = 4)
    private BigDecimal quantity;

    private BigDecimal totalDiscount;
    private BigDecimal itemDiscount;
    private String discount;
    private BigDecimal unitQuantity;

    @Column(nullable = false, precision = 25, scale = 4)
    private BigDecimal cost;

    @Column(nullable = false, precision = 25, scale = 4)
    private BigDecimal subtotal;

    private Long productUnit;
    private Long tranUnit;
    private BigDecimal operationValue;
    private BigDecimal realUnitCost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Stores store;

    private String quantityBalance;
    private String quantityReceived;
}

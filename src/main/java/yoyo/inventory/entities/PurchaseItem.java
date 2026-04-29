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
    @Column(nullable = false, precision = 15, scale = 4)
    private Integer quantity;

    private BigDecimal totalDiscount;
    private BigDecimal itemDiscount;

    @Column(nullable = false, precision = 25, scale = 4)
    private BigDecimal costPrice;
    @Column(nullable = false, precision = 25, scale = 4)
    private BigDecimal subtotal;

    private String quantityBalance;
    private String quantityReceived;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Stores tblStore;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "purchase_id", nullable = false)
    private Purchases tblPurchase;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product tblProduct;

}

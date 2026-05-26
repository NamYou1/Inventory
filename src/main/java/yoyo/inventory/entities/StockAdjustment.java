package yoyo.inventory.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import yoyo.inventory.enums.AdjustmentStatus;
import yoyo.inventory.enums.AdjustmentType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "stock_adjustments", indexes = {
        @Index(name = "idx_adj_reference_no", columnList = "referenceNo"),
        @Index(name = "idx_adj_date", columnList = "adjustmentDate"),
        @Index(name = "idx_adj_status", columnList = "status"),
        @Index(name = "idx_adj_type", columnList = "adjustmentType"),
        @Index(name = "idx_adj_product", columnList = "product_id"),
        @Index(name = "idx_adj_store", columnList = "store_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockAdjustment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String referenceNo;
    @Enumerated(EnumType.STRING)
    private AdjustmentType adjustmentType;
    @Enumerated(EnumType.STRING)
    private AdjustmentStatus status;
    private String reason;
    private LocalDateTime adjustmentDate;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Stores store;
    @Column(precision = 15, scale = 4)
    private BigDecimal quantity;
    private String createdBy;
    private String updatedBy;
}

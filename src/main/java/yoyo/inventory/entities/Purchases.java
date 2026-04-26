package yoyo.inventory.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tbl_purchases")
@Entity

public class Purchases extends BaseEntity { // if you use audit base
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 55, nullable = false)
    private String reference;

    private LocalDateTime date;
    @Column(length = 1000) private String note;
    private BigDecimal total;
    private String attachment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id")
    private Suppliers supplierEntity;

    private Short received;
    @Column(name = "created_by", nullable = false) private Long createdByUserId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Stores store;

    @Column(name = "product_discount") private BigDecimal productDiscount;
    private BigDecimal orderDiscount;
    private BigDecimal totalDiscount;
    private Integer deleteFlag;
    private String deleteBy;
    private Long updateBy;
    private LocalDateTime updateAt;
    private Integer no;
    private String supplier;
    private String purchasesStatus;
    private BigDecimal grandTotal;
    private BigDecimal paid;
    private String paymentStatus;

    @OneToMany(mappedBy = "purchase")
    private List<PurchaseItem> tblPurchaseItem ;
}

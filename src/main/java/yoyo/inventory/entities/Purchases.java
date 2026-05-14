package yoyo.inventory.entities;

import jakarta.persistence.*;
import lombok.*;
import yoyo.inventory.entities.status.PaymentStatus;
import yoyo.inventory.entities.status.PurchaseStatus;
import yoyo.inventory.entities.status.Status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tbl_purchases")
@Entity
public class Purchases extends BaseEntity { // if you use audit base
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 100, nullable = false)
    private String reference;
    private LocalDateTime date ;
    @Column(length = 1000)
    private String note;
    private BigDecimal total;
    private String no;
    private BigDecimal totalDiscount;
    private BigDecimal grandTotal;

    // status
    @Enumerated(EnumType.STRING)
    private Status status ;
    @Enumerated(EnumType.STRING)
    private PurchaseStatus purchaseStatus ;
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    // relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Stores tblStore;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private  Seller tblSeller;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id" , nullable = false)
    private Suppliers tblSuppliers;
    @OneToMany(mappedBy = "tblPurchase", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PurchaseItem> tblPurchaseItem ;

//    private String attachment;
//    private Short received;



//    private BigDecimal productDiscount;
//    private BigDecimal orderDiscount;
//    private BigDecimal totalDiscount;

//    private BigDecimal paid;
    // status

    // relationships


}

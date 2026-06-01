package yoyo.inventory.entities;

import jakarta.persistence.*;
import lombok.*;
import yoyo.inventory.enums.SaleStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sales", indexes = {
        @Index(name = "idx_sale_invoice_no", columnList = "invoiceNo"),
        @Index(name = "idx_sale_date", columnList = "saleDate"),
        @Index(name = "idx_sale_status", columnList = "status"),
        @Index(name = "idx_sale_store", columnList = "store_id"),
        @Index(name = "idx_sale_customer", columnList = "customer_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Sale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String invoiceNo;

    private LocalDateTime saleDate;

    @Enumerated(EnumType.STRING)
    private SaleStatus status;

    @Column(precision = 25, scale = 4)
    private BigDecimal subTotal;

    @Column(precision = 25, scale = 4)
    private BigDecimal discountAmount;

    @Column(precision = 25, scale = 4)
    private BigDecimal taxAmount;

    @Column(precision = 25, scale = 4)
    private BigDecimal totalAmount;

    private String note;

    private String createdBy;

    private String updatedBy;

    // =====================================
    // RELATIONSHIP
    // =====================================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Stores store;
    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<SaleItem> items = new ArrayList<>();
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;
    @OneToOne(mappedBy = "sale", cascade = CascadeType.ALL)
    private Invoice invoice;
}

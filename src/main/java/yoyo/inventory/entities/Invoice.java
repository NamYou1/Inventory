package yoyo.inventory.entities;
import jakarta.persistence.*;
import lombok.*;
import yoyo.inventory.enums.InvoiceStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "invoices", indexes = {
        @Index(name = "idx_invoice_no", columnList = "invoiceNo"),
        @Index(name = "idx_invoice_date", columnList = "invoiceDate"),
        @Index(name = "idx_invoice_due_date", columnList = "dueDate"),
        @Index(name = "idx_invoice_status", columnList = "status"),
        @Index(name = "idx_invoice_customer", columnList = "customer_id"),
        @Index(name = "idx_invoice_sale", columnList = "sale_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String invoiceNo;

    private LocalDateTime invoiceDate;

    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    private InvoiceStatus status;

    // ==========================
    // AMOUNT
    // ==========================

    @Column(precision = 25, scale = 4)
    private BigDecimal subTotal;

    @Column(precision = 25, scale = 4)
    private BigDecimal discountAmount;

    @Column(precision = 25, scale = 4)
    private BigDecimal taxAmount;

    @Column(precision = 25, scale = 4)
    private BigDecimal paidAmount;

    @Column(precision = 25, scale = 4)
    private BigDecimal balanceDue;

    @Column(precision = 25, scale = 4)
    private BigDecimal grandTotal;

    private String remark;

    // ==========================
    // RELATIONSHIP
    // ==========================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sale_id")
    private Sale sale;

    @OneToMany(
            mappedBy = "invoice",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Payment> payments ;
}

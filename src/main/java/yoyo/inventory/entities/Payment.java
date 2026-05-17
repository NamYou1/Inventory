package yoyo.inventory.entities;
import jakarta.persistence.*;
import lombok.*;
import yoyo.inventory.enums.InvoiceStatus;
import yoyo.inventory.enums.PaymentMethod;
import yoyo.inventory.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String paymentNo;

    private LocalDateTime paymentDate;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @Column(precision = 25, scale = 4)
    private BigDecimal amount;

    private String transactionNo;

    private String note;

    private String createdBy;

    // ==========================
    // RELATIONSHIP
    // ==========================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id")
    private Invoice invoice;
}
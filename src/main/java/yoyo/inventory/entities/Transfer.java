package yoyo.inventory.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import yoyo.inventory.entities.status.TransferStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "transfers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transfer extends  BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "transfer_no", nullable = false, unique = true, length = 55)
    private String transferNo;

    @Column(name = "date", nullable = false)
    private LocalDateTime date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_store_id", nullable = false)
    private Stores fromStoreId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_store_id", nullable = false)
    private Stores toStoreId;
    @Column(name = "note", length = 250)
    private String note;

    @Column(name = "total", nullable = false, precision = 25, scale = 4)
    private BigDecimal total = BigDecimal.ZERO;

    @Column(name = "shipping", nullable = false, precision = 25, scale = 4)
    private BigDecimal shipping = BigDecimal.ZERO;

    @Column(name = "grand_total", nullable = false, precision = 25, scale = 4)
    private BigDecimal grandTotal = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 55)
    private TransferStatus status = TransferStatus.PENDING;

    @Column(name = "attachment", length = 255)
    private String attachment;

    @Column(name = "sequence_no")
    private Short sequenceNo;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    @OneToMany(mappedBy = "transfer", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<TransferItem> items = new ArrayList<>();

    // Helper method to sync items
    public void setItems(List<TransferItem> items) {
        this.items.clear();
        if (items != null) {
            items.forEach(item -> item.setTransfer(this));
            this.items.addAll(items);
        }
    }

    // Helper method to recalculate totals
    public void recalculateTotals() {
        this.total = items.stream()
                .map(TransferItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        this.grandTotal = this.total.add(this.shipping);
    }
}

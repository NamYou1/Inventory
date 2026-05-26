package yoyo.inventory.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import yoyo.inventory.entities.status.Status;
import yoyo.inventory.entities.status.TransferStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "tbl_transfer", indexes = {
        @Index(name = "idx_transfer_no", columnList = "transfer_no"),
        @Index(name = "idx_transfer_date", columnList = "date"),
        @Index(name = "idx_transfer_status", columnList = "status"),
        @Index(name = "idx_transfer_active", columnList = "isActive"),
        @Index(name = "idx_transfer_from_store", columnList = "from_store_id"),
        @Index(name = "idx_transfer_to_store", columnList = "to_store_id")
})
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
    @Column(name = "note", length = 250)
    private String note;
    @Column(name = "total", nullable = false, precision = 25, scale = 4)
    private BigDecimal total = BigDecimal.ZERO;
    @Column(name = "grand_total", nullable = false, precision = 25, scale = 4)
    private BigDecimal grandTotal = BigDecimal.ZERO;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 55)
    private TransferStatus status ;
    @Column(name = "attachment", length = 255)
    private String attachment;
    @Enumerated(EnumType.STRING)
    private Status isActive ;

    // relationship
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_store_id", nullable = false)
    private Stores fromStoreId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_store_id", nullable = false)
    private Stores toStoreId;
    @OneToMany(mappedBy = "transfer", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<TransferItem> items ;

    // Helper method to sync items
//    public void setItems(List<TransferItem> items) {
//        this.items.clear();
//        if (items != null) {
//            items.forEach(item -> item.setTransfer(this));
//            this.items.addAll(items);
//        }
//    }


}

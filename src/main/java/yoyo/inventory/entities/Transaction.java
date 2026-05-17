package yoyo.inventory.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import yoyo.inventory.enums.SaleStatus;
import yoyo.inventory.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tbl_transaction", indexes = {
        @Index(name = "idx_tran_date", columnList = "tranDate"),
        @Index(name = "idx_tran_type_ref_id", columnList = "transactionType, referenceId"),
        @Index(name = "idx_tran_product_store", columnList = "product_id, store_id , unit_id")
})
public class Transaction extends  BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id ;
    private LocalDateTime tranDate ;
    private  String referenceNo ;
    // this is reference to another table like sale , purchase , adjustment , transfer
    private TransactionType transactionType ;
    private  Long referenceId ;
    private BigDecimal quantity ;
    private  BigDecimal unitQuantity ;
    private  BigDecimal pricePerUnit ;
    private  BigDecimal totalAmount ;
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private SaleStatus status ;
    private  String createdBy ;
    // relationship with another table
    @ManyToOne(fetch =  FetchType.LAZY)
    @JoinColumn(name = "store_id" , nullable = false)
    private  Stores tblStore ;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id" , nullable = false)
    private  Unit tblUnit  ;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id" , nullable = false)
    private  Product tblProduct ;

}

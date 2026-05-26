package yoyo.inventory.entities;

import jakarta.persistence.*;
import lombok.*;
import yoyo.inventory.entities.status.Status;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tbl_unit", indexes = {
        @Index(name = "idx_unit_code", columnList = "code"),
        @Index(name = "idx_unit_name", columnList = "name"),
        @Index(name = "idx_unit_status", columnList = "status")
})
public class Unit extends  BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id ;
    private  Integer baseUnit ;
    @Column(length = 50 , unique = true , nullable = false)
    private  String code ;
    @Column(length = 50 , unique = true , nullable = false)
    private  String name ;
    private  String operation;
    private  Integer operationValue ;
    @Enumerated(EnumType.STRING)
    private Status status ;
    @OneToMany(mappedBy = "tblUnit")
    private List<Product> products;
    @OneToMany(mappedBy = "tblUnit")
    private  List<PurchaseItem> purchaseItems ;
   @OneToMany(mappedBy = "unit") private List<TransferItem> transferItems ;}

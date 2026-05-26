package yoyo.inventory.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import yoyo.inventory.entities.status.Status;

@Data

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tbl_suppliers", indexes = {
        @Index(name = "idx_supplier_name", columnList = "name"),
        @Index(name = "idx_supplier_email", columnList = "email"),
        @Index(name = "idx_supplier_phone", columnList = "phone"),
        @Index(name = "idx_supplier_status", columnList = "status")
})
public class Suppliers {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private  Long id ;
    @Column(length = 50 , unique = true , nullable = false)
    private String name ;
    @Column(length = 50 , unique = true , nullable = false)
    private String email ;
    private  String phone ;
    private  String address ;
    @Enumerated(EnumType.STRING)
    private Status status ;
}

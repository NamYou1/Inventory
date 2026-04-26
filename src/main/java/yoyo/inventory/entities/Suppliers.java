package yoyo.inventory.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tbl_suppliers")
public class Suppliers {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private  Long id ;
    @Column(length = 50 , unique = true , nullable = false)
    private String name ;
    @Column(length = 50 , unique = true , nullable = false)
    private String email ;
    @Column(length = 20 , unique = true )
    private  String phone ;
    private  String address ;
    @Column(length = 10)
    private  String status  = "ACTIVE";
}

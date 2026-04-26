package yoyo.inventory.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
@Entity
@Table(name = "tbl_seller")
public class Seller {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;
    @Column(length = 50)
    private  String name ;
    @Column(unique = true , length =  50)
    private  String email ;
    @Column(unique = true , length =  20)
    private  String phone ;
    @Column(length = 10)
    private  String status  = "ACTIVE";
}

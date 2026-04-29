package yoyo.inventory.entities;

import jakarta.persistence.*;
import lombok.*;
import yoyo.inventory.entities.status.Status;

import java.util.List;

@AllArgsConstructor
@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "tbl_seller")
public class Seller extends  BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;
    @Column(length = 50)
    private  String name ;
    @Column(unique = true , length =  50 , nullable = false)
    private  String email ;
    @Column(unique = true , length =  20 , nullable = false)
    private  String phone ;
    @Enumerated(EnumType.STRING)
    private Status status ;

    @OneToMany(mappedBy = "tblSeller")
    private List<Purchases> tblPurchase ;
}

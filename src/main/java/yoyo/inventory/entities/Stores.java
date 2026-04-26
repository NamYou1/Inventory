package yoyo.inventory.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tbl_stores")
public class Stores {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private  Long id ;
    @Column(length = 50 , unique = true , nullable = false)
    private  String name ;
    @Column(length = 50 , unique = true , nullable = false)
    private  String code ;

    private  String logo ;
    @Column(length = 50 , unique = true , nullable = false)
    private  String email;
    @Column(length = 20 , unique = true)
    private  String phone ;
    private  String address1;
    private  String address2 ;
    private  String city ;
    private  String  state ;
    private  String postalCode ;
    private  String country ;
    private  String receiptHeader ;
    private  String receiptFooter ;
    @Column(length =  10)
    private  String status  = "ACTIVE";
    @OneToMany(mappedBy = "tblStores")
    private List<Stock> tblStock ;
}

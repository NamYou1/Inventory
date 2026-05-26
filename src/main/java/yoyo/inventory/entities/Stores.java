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
@Table(name = "tbl_stores", indexes = {
        @Index(name = "idx_store_name", columnList = "name"),
        @Index(name = "idx_store_code", columnList = "code"),
        @Index(name = "idx_store_email", columnList = "email"),
        @Index(name = "idx_store_phone", columnList = "phone"),
        @Index(name = "idx_store_status", columnList = "status"),
        @Index(name = "idx_store_city_country", columnList = "city, country")
})
public class Stores extends  BaseEntity {
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
    private  String addressOne;
    private  String addressTwo ;
    private  String city ;
    private  String  state ;
    private  String postalCode ;
    private  String country ;
    private  String receiptHeader ;
    private  String receiptFooter ;

    @Enumerated(EnumType.STRING)
    private Status status;
    @OneToMany(mappedBy = "tblStore")
    private List<Stock> tblStock;


}

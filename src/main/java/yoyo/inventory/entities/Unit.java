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
@Table(name = "tbl_unit")
public class Unit {
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
    @Column(length = 10)
    private  String status = "ACTIVE" ;
    @OneToMany(mappedBy = "tblUnit")
    private List<Product> tblProduct;
}

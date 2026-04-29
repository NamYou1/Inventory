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
@Table(name = "tbl_unit")
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
}

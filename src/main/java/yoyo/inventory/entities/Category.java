package yoyo.inventory.entities;

import jakarta.persistence.*;
import lombok.*;
import yoyo.inventory.entities.status.Status;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tbl_category")
public class Category extends  BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id ;
    @Column(length =  50 , unique = true , nullable = false)
    private  String name ;
    private  String description ;

    @Enumerated(EnumType.STRING)
    @Column(length = 20 )
    private Status status;

    @OneToMany(mappedBy = "tblCategory")
    private  List<Product> tblProduct ;

    @OneToMany(mappedBy = "tblCategory")
    private List<SubCategory> tblSubCategory ;

}

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
@Table(name = "tbl_category")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id ;
    @Column(length =  50 , unique = true , nullable = false)
    private  String name ;
    private  String description ;
    @Column(length = 10  )
    private  String status = "ACTIVE";
    @OneToMany(mappedBy = "tblCategory")
    private List<SubCategory> tblSubCategory ;

}

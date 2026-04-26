package yoyo.inventory.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tbl_sub_category")
public class SubCategory {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private  Long id ;
    @Column(length = 50 , unique = true , nullable = false)
    private  String code ;
    @Column(length = 50 , unique = true , nullable = false)
    private  String name ;
    @Column(length =  10)
    private  String status = "ACTIVE" ;
    @JoinColumn(name = "category_id")
    @ManyToOne( fetch=FetchType.LAZY)
    private  Category tblCategory;
}

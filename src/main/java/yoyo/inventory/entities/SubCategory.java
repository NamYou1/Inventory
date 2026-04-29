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
@Table(name = "tbl_sub_category")
public class SubCategory extends  BaseEntity{
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private  Long id ;
    @Column(length = 50 , unique = true , nullable = false)
    private  String code ;
    @Column(length = 50 , unique = true , nullable = false)
    private  String name ;

    @Enumerated(EnumType.STRING)
    private Status status;

    @OneToMany(mappedBy = "tblSubCategory")
    private List<Product> tblProduct ;

    @JoinColumn(name = "category_id" , nullable = false)
    @ManyToOne( fetch=FetchType.LAZY )
    private  Category tblCategory;
}

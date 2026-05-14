package yoyo.inventory.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import lombok.*;
import yoyo.inventory.entities.status.Status;

import java.math.BigDecimal;
import java.util.List;
@Entity
@Table(name = "tbl_product")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, unique = true, nullable = false)
    private String code;

    @Column(length = 50, unique = true, nullable = false)
    private String name;

    @Column(length = 50)
    private String otherName;

    @DecimalMin("0.0")
    private BigDecimal salePrice;

    @DecimalMin("0.0")
    private BigDecimal costPrice;

    private Integer taxMethod;

    private String barCodeSymbology;

    private String type;

    private String details;

    private Integer alertQuantity = 0;

    @Column(length = 255)
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private  Category tblCategory ;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id")
    private Unit tblUnit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subcategory_id")
    private SubCategory tblSubCategory;

    @OneToMany(mappedBy = "tblProduct")
    private  List<PurchaseItem> tblPurchseItem;

    @OneToMany(mappedBy = "tblProduct", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Stock> stocks;


}

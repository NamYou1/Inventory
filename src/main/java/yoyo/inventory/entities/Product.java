package yoyo.inventory.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tbl_product")
public class Product {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id ;
    @Column(length = 50 , unique = true , nullable = false)
    private  String code ;
    @Column(length = 50 , unique = true , nullable = false)
    private  String name ;
    @Column(length = 50 , unique = true , nullable = false)
    private  String otherName ;

    private BigDecimal salePrice ;
    private  BigDecimal costPrice ;
    private  Integer taxMethod ;
    private  String barCodeSymbology ;
    private  String type ;
    private  String details ;
    private  Integer alertQuantity ;
    private  Integer defaultSaleUnit ;
    private  Integer printer ;
    private  String image ;
    @Column(length =  10 )
    private  String status = "ACTIVE" ;

    @JoinColumn(name = "unit_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Unit tblUnit ;
    @JoinColumn(name = "subcategory_id")
    @ManyToOne(fetch =  FetchType.LAZY)
    private  SubCategory tblSubCategory ;
    @OneToMany( mappedBy = "tblProduct")
    @JoinColumn(name = "stock_id")
    private List<Stock> tblStock ;


}

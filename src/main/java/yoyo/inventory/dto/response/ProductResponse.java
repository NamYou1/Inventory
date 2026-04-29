package yoyo.inventory.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class ProductResponse {
    private Long id;
    private String code;
    private String name;
    private String otherName;
    private BigDecimal salePrice ;
    private BigDecimal costPrice ;
    private  Integer taxMethod ;
    private  String barCodeSymbology;
    private  String type ;
    private  String details ;
    private  Integer alertQuantity ;

    private Long unitId;
    private  String unitName ;

    private  Long categoryId ;
    private  String categoryName ;

    private  Long subCategoryId;
    private String subCategoryName;

    private String imageUrl;
    private  String status ;


}

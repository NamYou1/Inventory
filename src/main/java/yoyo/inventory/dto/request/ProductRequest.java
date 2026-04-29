package yoyo.inventory.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import yoyo.inventory.entities.status.Status;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequest {
    @NotNull(message = "code is required")
    private  String code ;
    private  String name ;
    private  String otherName ;
    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal salePrice;

    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal costPrice;
    private  Integer taxMethod ;
    private  String barCodeSymbology ;
    private  String type  ;
    private  String details ;
    private  Integer alertQuantity ;

    private String imageUrl ;
    @NotNull(message = "unitId is required")
    private  Long unitId;
    @NotNull(message = "categoryId is required")
    private  Long categoryId;
    @NotNull(message = "subCategoryId is required")
    private  Long subCategoryId ;

    private Status status = Status.ACTIVE;

}

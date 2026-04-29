package yoyo.inventory.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class SubCategoryResponse {
    private  Long id ;
    private  String code ;
    private  String name ;
    private  String status ;
    private  Long categoryId;
    private  String categoryName ;
}

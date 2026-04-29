package yoyo.inventory.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SellerResponse {
    private Long id ;
    private String name ;
    private String email ;
    private String phone ;
    private String status ;
}

package yoyo.inventory.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SupplierResponse {
    private  Long id ;
    private  String name ;
    private  String email ;
    private  String address ;
    private  String status ;
}

package yoyo.inventory.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StoreResponse {
    private  Long id ;
    private  String name;
    private  String code ;
    private  String logo ;
    private  String email;
    private  String phone ;
    private  String addressOne ;
    private  String addressTwo;
    private  String city ;
    private  String state ;
    private  String  postalCode ;
    private  String  country ;
    private  String receiptHeader ;
    private  String receiptFooter ;
    private  String status ;
}

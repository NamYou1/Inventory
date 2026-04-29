package yoyo.inventory.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UnitResponse {
    private  Long id ;
    private  Integer baseUnit ;
    private  String code ;
    private  String name ;
    private  String operation ;
    private  String operationValue ;
    private  String status ;
}

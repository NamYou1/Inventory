package yoyo.inventory.specification.store;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StoreFilter {
    private  String name;
    private  String code ;
    private  String email ;
    private  String phone ;

}

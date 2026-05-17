package yoyo.inventory.specification.adjustment;

import lombok.Data;

import java.util.Date;

@Data
public class AdjustmentFilter {
    private  String referenceNo;
    private Date adjustmentDate;
    private  Long productId  ;
    private  Long storeId ;
}

package yoyo.inventory.specification.sale;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import yoyo.inventory.enums.SaleStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SaleFilter {
    private String invoiceNo;
    private Long storeId;
    private Long customerId;
    private SaleStatus status;
    private String note;
}

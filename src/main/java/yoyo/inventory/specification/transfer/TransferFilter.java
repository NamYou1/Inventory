package yoyo.inventory.specification.transfer;

import lombok.Data;

import java.util.Date;

@Data
public class TransferFilter {
    private  String transferNo ;
    private  Long fromStoreId ;
    private  Long toStoreId ;
    private Date fromDate ;
}

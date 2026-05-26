package yoyo.inventory.dto.response;

import lombok.*;
import yoyo.inventory.entities.status.TransferStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class TransferResponse {

    private Long id;
    private String transferNo;
    private LocalDateTime date;
    private Long fromStoreId;
    private Long toStoreId;
    private String note;
    private BigDecimal total;
    private BigDecimal grandTotal;
    private String status;
    private String attachment;
    private String createdBy;
    private String updatedBy;
    private  String isActive  ;
    private List<TransferItemResponse> items;
}
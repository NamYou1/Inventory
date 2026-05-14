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
@Builder
public class TransferResponse {

    private Long id;
    private String transferNo;
    private LocalDateTime date;
    private Long fromStoreId;
    private Long toStoreId;
    private String note;
    private BigDecimal total;
    private BigDecimal shipping;
    private BigDecimal grandTotal;
    private TransferStatus status;
    private String attachment;
    private Short sequenceNo;
    private Boolean isDeleted;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<TransferItemResponse> items;
}
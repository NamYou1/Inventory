package yoyo.inventory.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import yoyo.inventory.entities.status.TransferStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransferFilterRequest {
    private String transferNo;
    private Integer fromStoreId;
    private Integer toStoreId;
    private TransferStatus status;
    private LocalDateTime fromDate;
    private LocalDateTime toDate;
}

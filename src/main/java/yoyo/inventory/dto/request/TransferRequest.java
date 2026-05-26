package yoyo.inventory.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import yoyo.inventory.entities.status.Status;
import yoyo.inventory.entities.status.TransferStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferRequest {
    private LocalDateTime date;
    @NotNull(message = "From store is required")
    private Long fromStoreId;
    @NotNull(message = "To store is required")
    private Long toStoreId;
    @Size(max = 250)
    private String note;
    private TransferStatus status;
    @Size(max = 255)
    private String attachment;
    private Status isActive = Status.ACTIVE;
    @NotEmpty(message = "Transfer must have at least one item")
    @Valid
    private List<TransferItemRequest> items;
}
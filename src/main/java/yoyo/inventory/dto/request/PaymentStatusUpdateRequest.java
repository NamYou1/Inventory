package yoyo.inventory.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import yoyo.inventory.entities.status.PaymentStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentStatusUpdateRequest {
    @NotNull(message = "Payment status is required")
    private PaymentStatus status;
}


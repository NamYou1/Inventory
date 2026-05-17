package yoyo.inventory.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import yoyo.inventory.enums.PaymentMethod;

import java.math.BigDecimal;

@Data
public class PaymentRequest {

    @NotNull
    private Long invoiceId;

    @NotNull
    private BigDecimal amount;

    @NotNull
    private PaymentMethod paymentMethod;

    private String transactionNo;

    private String note;
}
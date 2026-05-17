package yoyo.inventory.dto.response;

import lombok.Builder;
import lombok.Data;
import yoyo.inventory.enums.PaymentMethod;
import yoyo.inventory.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class PaymentResponse {

    private Long id;

    private String paymentNo;

    private LocalDateTime paymentDate;

    private BigDecimal amount;

    private PaymentMethod paymentMethod;

    private PaymentStatus status;

    private String invoiceNo;

    private String customerName;
}
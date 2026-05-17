package yoyo.inventory.dto.response;

import lombok.Builder;
import lombok.Data;
import yoyo.inventory.enums.InvoiceStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class InvoiceResponse {

    private Long id;

    private String invoiceNo;

    private LocalDateTime invoiceDate;

    private InvoiceStatus status;

    private String customerName;

    private String saleInvoiceNo;

    private BigDecimal grandTotal;

    private BigDecimal paidAmount;

    private BigDecimal balanceDue;
}
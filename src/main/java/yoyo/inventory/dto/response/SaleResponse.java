package yoyo.inventory.dto.response;

import lombok.Builder;
import lombok.Data;
import yoyo.inventory.enums.SaleStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class SaleResponse {

    private Long id;

    private String invoiceNo;

    private LocalDateTime saleDate;

    private SaleStatus status;

    private String storeName;

    private BigDecimal subTotal;

    private BigDecimal discountAmount;

    private BigDecimal taxAmount;

    private BigDecimal totalAmount;

    private String note;

    private List<SaleItemResponse> items;
}
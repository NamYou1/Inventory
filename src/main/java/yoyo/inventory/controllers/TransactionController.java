package yoyo.inventory.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import yoyo.inventory.common.Message;
import yoyo.inventory.constants.ErrorCode;
import yoyo.inventory.dto.response.TransactionResponse;
import yoyo.inventory.dto.response.TransactionSummaryReportResponse;
import yoyo.inventory.entities.Transaction;
import yoyo.inventory.enums.TransactionType;
import yoyo.inventory.execption.ApiResponse;
import yoyo.inventory.mappers.TransactionMapper;
import yoyo.inventory.services.TransactionService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
@Tag(name = "Transaction Tracking", description = "Endpoints for audit logs and historical tracking of all stock movements")
public class TransactionController {

    private final TransactionService transactionService;
    private final TransactionMapper transactionMapper;

    @GetMapping("/reports")
    @PreAuthorize("hasAuthority('report:read')")
    @Operation(summary = "Get transaction history by date range", description = "Retrieves stock movements between two dates, perfect for auditing and reporting.")
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> getReportsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate
    ) {
        List<Transaction> transactions = transactionService.getReportsByDateRange(startDate, endDate);
        List<TransactionResponse> payload = transactions.stream()
                .map(transactionMapper::toResponse)
                .toList();

        ApiResponse<List<TransactionResponse>> response = ApiResponse.<List<TransactionResponse>>builder()
                .success(ErrorCode.SUCCESS)
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .message("Transaction history retrieved successfully for date range")
                .payload(payload)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/type/{type}")
    @PreAuthorize("hasAuthority('report:read')")
    @Operation(summary = "Get transaction history by type", description = "Filters stock movements by specific type (e.g. SALE, PURCHASE, TRANSFER_IN, TRANSFER_OUT, ADJUSTMENT).")
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> getReportsByType(
            @PathVariable TransactionType type
    ) {
        List<Transaction> transactions = transactionService.getReportsByType(type);
        List<TransactionResponse> payload = transactions.stream()
                .map(transactionMapper::toResponse)
                .toList();

        ApiResponse<List<TransactionResponse>> response = ApiResponse.<List<TransactionResponse>>builder()
                .success(ErrorCode.SUCCESS)
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .message("Transaction history retrieved successfully for type: " + type)
                .payload(payload)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/summary")
    @PreAuthorize("hasAuthority('report:read')")
    @Operation(summary = "Get transaction summary report", description = "Returns aggregated counts and amounts for sales, purchases, and transfers (period: today, week, month, year).")
    public ResponseEntity<ApiResponse<TransactionSummaryReportResponse>> getSummaryReport(
            @RequestParam(value = "period", defaultValue = "today") String period
    ) {
        TransactionSummaryReportResponse summary = transactionService.getSummaryReport(period);

        ApiResponse<TransactionSummaryReportResponse> response = ApiResponse.<TransactionSummaryReportResponse>builder()
                .success(ErrorCode.SUCCESS)
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .message("Transaction summary report retrieved successfully for period: " + period)
                .payload(summary)
                .build();

        return ResponseEntity.ok(response);
    }
}

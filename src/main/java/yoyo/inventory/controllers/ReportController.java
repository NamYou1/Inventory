package yoyo.inventory.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import yoyo.inventory.dto.response.TransactionSummaryReportResponse;
import yoyo.inventory.execption.ApiResponse;
import yoyo.inventory.services.TransactionService;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {

    private final TransactionService transactionService;

    @GetMapping("/summary")
    @PreAuthorize("hasAuthority('report:read')")
    public ApiResponse<TransactionSummaryReportResponse> getSummary(
            @RequestParam(defaultValue = "today") String period
    ) {
        return ApiResponse.<TransactionSummaryReportResponse>builder()
                .success("true")
                .status(HttpStatus.OK)
                .message("Report generated successfully")
                .payload(transactionService.getSummaryReport(period))
                .timestamp(LocalDateTime.now())
                .build();
    }
}

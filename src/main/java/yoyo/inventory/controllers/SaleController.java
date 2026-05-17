package yoyo.inventory.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import yoyo.inventory.dto.request.SaleRequest;
import yoyo.inventory.dto.response.SaleResponse;
import yoyo.inventory.enums.SaleStatus;
import yoyo.inventory.execption.ApiResponse;
import yoyo.inventory.services.SaleService;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/sales")
@RequiredArgsConstructor
public class SaleController {

    private final SaleService saleService;

    @PostMapping
    public ApiResponse<SaleResponse> create(
            @RequestBody SaleRequest request
    ) {

        return ApiResponse.<SaleResponse>builder()
                .success("true")
                .status(HttpStatus.CREATED)
                .message("Sale created successfully")
                .payload(
                        saleService.create(
                                request,
                                "admin"
                        )
                )
                .timestamp(LocalDateTime.now())
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<SaleResponse> getById(
            @PathVariable Long id
    ) {

        return ApiResponse.<SaleResponse>builder()
                .success("true")
                .status(HttpStatus.OK)
                .message("Sale retrieved successfully")
                .payload(
                        saleService.getById(id)
                )
                .timestamp(LocalDateTime.now())
                .build();
    }

    @GetMapping
    public ApiResponse<Page<SaleResponse>> getAll(
            Pageable pageable
    ) {

        return ApiResponse.<Page<SaleResponse>>builder()
                .success("true")
                .status(HttpStatus.OK)
                .message("Sale list retrieved successfully")
                .payload(
                        saleService.getAll(pageable)
                )
                .timestamp(LocalDateTime.now())
                .build();
    }

    @PatchMapping("/{id}/status")
    public ApiResponse<SaleResponse> updateStatus(
            @PathVariable Long id,
            @RequestParam SaleStatus status
    ) {

        return ApiResponse.<SaleResponse>builder()
                .success("true")
                .status(HttpStatus.OK)
                .message("Sale status updated successfully")
                .payload(
                        saleService.updateStatus(
                                id,
                                status,
                                "admin"
                        )
                )
                .timestamp(LocalDateTime.now())
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(
            @PathVariable Long id
    ) {

        saleService.delete(id);

        return ApiResponse.<Void>builder()
                .success("true")
                .status(HttpStatus.OK)
                .message("Sale deleted successfully")
                .timestamp(LocalDateTime.now())
                .build();
    }
}
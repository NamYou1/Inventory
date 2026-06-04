package yoyo.inventory.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import yoyo.inventory.dto.request.SaleRequest;
import yoyo.inventory.dto.response.SaleResponse;
import yoyo.inventory.enums.SaleStatus;
import yoyo.inventory.execption.ApiResponse;
import yoyo.inventory.services.SaleService;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/sales")
@RequiredArgsConstructor
public class SaleController {

    private final SaleService saleService;

    @PostMapping
    @PreAuthorize("hasAuthority('sale:create')")
    public ApiResponse<SaleResponse> create(
            @RequestBody SaleRequest request,
            Principal principal
    ) {

        return ApiResponse.<SaleResponse>builder()
                .success("true")
                .status(HttpStatus.CREATED)
                .message("Sale created successfully")
                .payload(
                        saleService.create(
                                request,
                                principal != null ? principal.getName() : "system"
                        )
                )
                .timestamp(LocalDateTime.now())
                .build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('sale:read')")
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
    @PreAuthorize("hasAuthority('sale:read')")
    public ApiResponse<Page<SaleResponse>> getAll(
            @RequestParam Map<String, String> params
    ) {

        return ApiResponse.<Page<SaleResponse>>builder()
                .success("true")
                .status(HttpStatus.OK)
                .message("Sale list retrieved successfully")
                .payload(
                        saleService.getAll(params)
                )
                .timestamp(LocalDateTime.now())
                .build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('sale:update')")
    public ApiResponse<SaleResponse> update(
            @PathVariable Long id,
            @RequestBody SaleRequest request,
            Principal principal
    ) {

        return ApiResponse.<SaleResponse>builder()
                .success("true")
                .status(HttpStatus.OK)
                .message("Sale updated successfully")
                .payload(
                        saleService.update(
                                id,
                                request,
                                principal != null ? principal.getName() : "system"
                        )
                )
                .timestamp(LocalDateTime.now())
                .build();
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('sale:update')")
    public ApiResponse<SaleResponse> updateStatus(
            @PathVariable Long id,
            @RequestParam SaleStatus status,
            Principal principal
    ) {

        return ApiResponse.<SaleResponse>builder()
                .success("true")
                .status(HttpStatus.OK)
                .message("Sale status updated successfully")
                .payload(
                        saleService.updateStatus(
                                id,
                                status,
                                principal != null ? principal.getName() : "system"
                        )
                )
                .timestamp(LocalDateTime.now())
                .build();
    }

    @PatchMapping("/{id}/complete")
    @PreAuthorize("hasAuthority('sale:approve')")
    public ApiResponse<SaleResponse> complete(@PathVariable Long id, Principal principal) {
        return ApiResponse.<SaleResponse>builder()
                .success("true")
                .status(HttpStatus.OK)
                .message("Sale completed successfully")
                .payload(saleService.complete(id, principal != null ? principal.getName() : "system"))
                .timestamp(LocalDateTime.now())
                .build();
    }

    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasAuthority('sale:update')")
    public ApiResponse<SaleResponse> cancel(@PathVariable Long id, Principal principal) {
        return ApiResponse.<SaleResponse>builder()
                .success("true")
                .status(HttpStatus.OK)
                .message("Sale cancelled successfully")
                .payload(saleService.cancel(id, principal != null ? principal.getName() : "system"))
                .timestamp(LocalDateTime.now())
                .build();
    }

    @PatchMapping("/{id}/return")
    @PreAuthorize("hasAuthority('sale:update')")
    public ApiResponse<SaleResponse> returnSale(@PathVariable Long id, Principal principal) {
        return ApiResponse.<SaleResponse>builder()
                .success("true")
                .status(HttpStatus.OK)
                .message("Sale returned successfully")
                .payload(saleService.returnSale(id, principal != null ? principal.getName() : "system"))
                .timestamp(LocalDateTime.now())
                .build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('sale:delete')")
    public ApiResponse<Void> delete(
            @PathVariable Long id,
            Principal principal
    ) {

        saleService.delete(id, principal != null ? principal.getName() : "system");

        return ApiResponse.<Void>builder()
                .success("true")
                .status(HttpStatus.OK)
                .message("Sale deleted successfully")
                .timestamp(LocalDateTime.now())
                .build();
    }
}


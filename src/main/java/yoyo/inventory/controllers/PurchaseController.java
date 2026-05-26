package yoyo.inventory.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import yoyo.inventory.common.Message;
import yoyo.inventory.constants.ErrorCode;
import yoyo.inventory.dto.request.PurchaseRequest;
import yoyo.inventory.dto.response.PurchaseResponse;
import yoyo.inventory.execption.ApiResponse;
import yoyo.inventory.services.PurchaseService;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@Tag(name = "Purchase", description = "Endpoints for managing purchases")
@RequestMapping("/api/v1/purchases")
public class PurchaseController {
    private  final PurchaseService purchaseService ;

    @PostMapping
    @PreAuthorize("hasAuthority('purchase:create')")
    public ResponseEntity<ApiResponse<PurchaseResponse>> createPurchase(@Valid @RequestBody PurchaseRequest request){
        PurchaseResponse category = purchaseService.create(request);
        ApiResponse<PurchaseResponse> response =ApiResponse.<PurchaseResponse>builder()
                .success(ErrorCode.SUCCESS)
                .status(HttpStatus.CREATED)
                .timestamp(LocalDateTime.now())
                .message(Message.created("Purchase"))
                .payload(category)
                .build();
        return  ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('purchase:read')")
    public ResponseEntity<ApiResponse<Page<PurchaseResponse>>> getAllPurchases(@RequestParam Map<String, String> params) {
        Page<PurchaseResponse> purchases = purchaseService.getAll(params);
        ApiResponse<Page<PurchaseResponse>> response = ApiResponse.<Page<PurchaseResponse>>builder()
                .success(ErrorCode.SUCCESS)
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .message(Message.getAll("Purchases"))
                .payload(purchases)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('purchase:read')")
    public ResponseEntity<ApiResponse<PurchaseResponse>> getById(@PathVariable Long id) {
        PurchaseResponse purchase = purchaseService.getById(id);
        ApiResponse<PurchaseResponse> response = ApiResponse.<PurchaseResponse>builder()
                .success(ErrorCode.SUCCESS)
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .message(Message.getById("Purchase", id))
                .payload(purchase)
                .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('purchase:update')")
    public ResponseEntity<ApiResponse<PurchaseResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody PurchaseRequest request,
            Principal principal
    ) {
        PurchaseResponse purchase = purchaseService.update(id, request, principal.getName());
        ApiResponse<PurchaseResponse> response = ApiResponse.<PurchaseResponse>builder()
                .success(ErrorCode.SUCCESS)
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .message(Message.updated("Purchase", id))
                .payload(purchase)
                .build();
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasAuthority('purchase:approve')")
    public ResponseEntity<ApiResponse<PurchaseResponse>> approve(@PathVariable Long id, Principal principal) {
        PurchaseResponse purchase = purchaseService.approve(id, principal != null ? principal.getName() : "system");
        ApiResponse<PurchaseResponse> response = ApiResponse.<PurchaseResponse>builder()
                .success(ErrorCode.SUCCESS)
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .message(Message.updated("Purchase", id))
                .payload(purchase)
                .build();
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/complete")
    @PreAuthorize("hasAuthority('purchase:approve')")
    public ResponseEntity<ApiResponse<PurchaseResponse>> complete(@PathVariable Long id, Principal principal) {
        PurchaseResponse purchase = purchaseService.complete(id, principal != null ? principal.getName() : "system");
        ApiResponse<PurchaseResponse> response = ApiResponse.<PurchaseResponse>builder()
                .success(ErrorCode.SUCCESS)
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .message(Message.updated("Purchase", id))
                .payload(purchase)
                .build();
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasAuthority('purchase:update')")
    public ResponseEntity<ApiResponse<PurchaseResponse>> cancel(@PathVariable Long id, Principal principal) {
        PurchaseResponse purchase = purchaseService.cancel(id, principal != null ? principal.getName() : "system");
        ApiResponse<PurchaseResponse> response = ApiResponse.<PurchaseResponse>builder()
                .success(ErrorCode.SUCCESS)
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .message(Message.updated("Purchase", id))
                .payload(purchase)
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('purchase:delete')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id, Principal principal) {
        purchaseService.delete(id, principal.getName());
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(ErrorCode.SUCCESS)
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .message(Message.deleted("Purchase", id))
                .build();
        return ResponseEntity.ok(response);
    }
}


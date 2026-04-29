package yoyo.inventory.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import yoyo.inventory.common.Message;
import yoyo.inventory.constants.ErrorCode;
import yoyo.inventory.dto.request.PurchaseRequest;
import yoyo.inventory.dto.response.PurchaseResponse;
import yoyo.inventory.execption.ApiResponse;
import yoyo.inventory.services.PurchaseService;

import java.time.Instant;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@RestController
@Tag(name = "Purchase", description = "Endpoints for managing purchases")
@RequestMapping("/api/v1/purchases")
public class PurchaseController {
    private  final PurchaseService purchaseService ;

    @PostMapping
    public ResponseEntity<ApiResponse<PurchaseResponse>> createPurchase(@Valid @RequestBody PurchaseRequest request){
        PurchaseResponse category = purchaseService.createPurchase(request);
        ApiResponse<PurchaseResponse> response =ApiResponse.<PurchaseResponse>builder()
                .success(ErrorCode.SUCCESS)
                .status(HttpStatus.CREATED)
                .timestamp(LocalDateTime.now())
                .message(Message.created("Purchase"))
                .payload(category)
                .build();
        return  ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}

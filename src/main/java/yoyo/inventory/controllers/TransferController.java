package yoyo.inventory.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import yoyo.inventory.dto.request.TransferRequest;
import yoyo.inventory.dto.response.TransferResponse;
import yoyo.inventory.execption.ApiResponse;
import yoyo.inventory.services.TransferService;

import java.security.Principal;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/transfers")
@RequiredArgsConstructor
public class TransferController {

    private final TransferService transferService;

    // CREATE
    @PostMapping
    public ResponseEntity<ApiResponse<TransferResponse>> create(
            @RequestBody TransferRequest request
//            Principal principal
    ) {
        TransferResponse response = transferService.create(request
//                principal.getName()
        );

        return ResponseEntity.ok(
                ApiResponse.<TransferResponse>builder()
                        .success("true")
                        .status(HttpStatus.CREATED)
                        .message("Transfer created successfully")
                        .payload(response)
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    // GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TransferResponse>> getById(@PathVariable Long id) {

        TransferResponse response = transferService.getById(id);

        return ResponseEntity.ok(
                ApiResponse.<TransferResponse>builder()
                        .success("true")
                        .status(HttpStatus.OK)
                        .message("Transfer fetched successfully")
                        .payload(response)
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    // LIST
//    @GetMapping
//    public ResponseEntity<ApiResponse<Page<TransferResponse>>> getAll(
//            TransferFilterRequest filter,
//            Pageable pageable
//    ) {
//
//        Page<TransferResponse> response = transferService.getAll(filter, pageable);
//
//        return ResponseEntity.ok(
//                ApiResponse.<Page<TransferResponse>>builder()
//                        .success("true")
//                        .status(HttpStatus.OK)
//                        .message("Transfers fetched successfully")
//                        .payload(response)
//                        .timestamp(LocalDateTime.now())
//                        .build()
//        );
//    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TransferResponse>> update(
            @PathVariable Long id,
            @RequestBody TransferRequest request,
            Principal principal
    ) {

        TransferResponse response =
                transferService.update(id, request, principal.getName());

        return ResponseEntity.ok(
                ApiResponse.<TransferResponse>builder()
                        .success("true")
                        .status(HttpStatus.OK)
                        .message("Transfer updated successfully")
                        .payload(response)
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    // APPROVE
    @PatchMapping("/{id}/approve")
    public ResponseEntity<ApiResponse<TransferResponse>> approve(
            @PathVariable Long id,
            Principal principal
    ) {

        return ResponseEntity.ok(
                ApiResponse.<TransferResponse>builder()
                        .success("true")
                        .status(HttpStatus.OK)
                        .message("Transfer approved")
                        .payload(transferService.approve(id, principal.getName()))
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    // COMPLETE
    @PatchMapping("/{id}/complete")
    public ResponseEntity<ApiResponse<TransferResponse>> complete(
            @PathVariable Long id,
            Principal principal
    ) {

        return ResponseEntity.ok(
                ApiResponse.<TransferResponse>builder()
                        .success("true")
                        .status(HttpStatus.OK)
                        .message("Transfer completed")
                        .payload(transferService.complete(id, principal.getName()))
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    // CANCEL
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<TransferResponse>> cancel(
            @PathVariable Long id,
            Principal principal
    ) {

        return ResponseEntity.ok(
                ApiResponse.<TransferResponse>builder()
                        .success("true")
                        .status(HttpStatus.OK)
                        .message("Transfer cancelled")
                        .payload(transferService.cancel(id, principal.getName()))
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id,
            Principal principal
    ) {

        transferService.delete(id, principal.getName());

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success("true")
                        .status(HttpStatus.OK)
                        .message("Transfer deleted")
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }
}
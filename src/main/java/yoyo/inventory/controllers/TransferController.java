package yoyo.inventory.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import yoyo.inventory.common.Message;
import yoyo.inventory.common.PageDTO;
import yoyo.inventory.constants.ErrorCode;
import yoyo.inventory.dto.request.TransferRequest;
import yoyo.inventory.dto.response.TransferResponse;
import yoyo.inventory.execption.ApiResponse;
import yoyo.inventory.services.TransferService;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/transfers")
@RequiredArgsConstructor
@Tag(name = "Transfer Management", description = "APIs for managing inventory transfers between stores")
public class TransferController {

    private final TransferService transferService;

    @GetMapping
    @PreAuthorize("hasAuthority('transfer:read')")
    public ResponseEntity<ApiResponse<PageDTO>>getAll(@RequestParam Map<String , String> params){
        Page<TransferResponse> responses = transferService.getAll(params);
        PageDTO pageDTO =  new PageDTO(responses);
        ApiResponse<PageDTO> response = ApiResponse.<PageDTO>builder()
                .success(ErrorCode.SUCCESS)
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .message(Message.getAll("Transfer"))
                .payload(pageDTO)
                .build();
        return ResponseEntity.ok(response);
    }


    // CREATE
    @PostMapping
    @PreAuthorize("hasAuthority('transfer:create')")
    public ResponseEntity<ApiResponse<TransferResponse>> create(@RequestBody TransferRequest request) {
        TransferResponse response = transferService.create(request);

        return ResponseEntity.ok(
                ApiResponse.<TransferResponse>builder()
                        .success(ErrorCode.SUCCESS)
                        .status(HttpStatus.CREATED)
                        .message(Message.created("Transfer"))
                        .payload(response)
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    // GET BY ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('transfer:read')")
    public ResponseEntity<ApiResponse<TransferResponse>> getById(@PathVariable Long id) {

        TransferResponse response = transferService.getById(id);
        return ResponseEntity.ok(
                ApiResponse.<TransferResponse>builder()
                        .success(ErrorCode.SUCCESS)
                        .status(HttpStatus.OK)
                        .message(Message.created("Transfer"))
                        .payload(response)
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    // UPDATE
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('transfer:update')")
    public ResponseEntity<ApiResponse<TransferResponse>> update(@PathVariable Long id, @RequestBody TransferRequest request, Principal principal) {
        TransferResponse response = transferService.update(id, request, principal != null ? principal.getName() : "system");
        return ResponseEntity.ok(
                ApiResponse.<TransferResponse>builder()
                        .success(ErrorCode.SUCCESS)
                        .status(HttpStatus.OK)
                        .message(Message.updated("Transfer" , id))
                        .payload(response)
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    // APPROVE
    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasAuthority('transfer:approve')")
    public ResponseEntity<ApiResponse<TransferResponse>> approve(@PathVariable Long id, Principal principal) {

        return ResponseEntity.ok(
                ApiResponse.<TransferResponse>builder()
                        .success(ErrorCode.SUCCESS)
                        .status(HttpStatus.OK)
                        .message(Message.updated("Transfer" , id))
                        .payload(transferService.approve(id, principal != null ? principal.getName() : "system"))
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    // COMPLETE
    @PatchMapping("/{id}/complete")
    @PreAuthorize("hasAuthority('transfer:approve')")
    public ResponseEntity<ApiResponse<TransferResponse>> complete(@PathVariable Long id, Principal principal) {
        return ResponseEntity.ok(
                ApiResponse.<TransferResponse>builder()
                        .success(ErrorCode.SUCCESS)
                        .status(HttpStatus.OK)
                        .message(Message.updated("Transfer"  , id))
                        .payload(transferService.complete(id, principal != null ? principal.getName() : "system"))
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    // CANCEL
    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasAuthority('transfer:update')")
    public ResponseEntity<ApiResponse<TransferResponse>> cancel(@PathVariable Long id, Principal principal) {
        return ResponseEntity.ok(
                ApiResponse.<TransferResponse>builder()
                        .success(ErrorCode.SUCCESS)
                        .status(HttpStatus.OK)
                        .message(Message.updated("Transfer"  , id))
                        .payload(transferService.cancel(id, principal != null ? principal.getName() : "system"))
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }
    // DELETE
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('transfer:delete')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id, Principal principal) {
        transferService.delete(id, principal != null ? principal.getName() : "system");
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(ErrorCode.SUCCESS)
                        .status(HttpStatus.OK)
                        .message(Message.deleted("Transfer" , id))
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }
}

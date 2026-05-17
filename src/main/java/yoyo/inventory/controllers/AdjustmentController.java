package yoyo.inventory.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import yoyo.inventory.common.Message;
import yoyo.inventory.common.PageDTO;
import yoyo.inventory.constants.ErrorCode;
import yoyo.inventory.dto.request.AdjustmentRequest;
import yoyo.inventory.dto.response.AdjustmentResponse;
import yoyo.inventory.enums.AdjustmentStatus;
import yoyo.inventory.execption.ApiResponse;
import yoyo.inventory.services.AdjustmentService;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/adjustments")
@RequiredArgsConstructor
public class AdjustmentController {

    private final AdjustmentService adjustmentService;

    @PostMapping
    public ApiResponse<AdjustmentResponse> create(@RequestBody AdjustmentRequest request) {

        return ApiResponse.<AdjustmentResponse>builder()
                .success(ErrorCode.SUCCESS)
                .status(HttpStatus.CREATED)
                .timestamp(LocalDateTime.now())
                .message(Message.created("Adjustment"))
                .payload(adjustmentService.create(request, "admin"))
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<AdjustmentResponse> getById(@PathVariable Long id) {
        return ApiResponse.<AdjustmentResponse>builder()
                .success(ErrorCode.SUCCESS)
                .status(HttpStatus.OK)
                .message(Message.getById("Adjustment", id))
                .payload(adjustmentService.getById(id))
                .timestamp(LocalDateTime.now())
                .build();
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageDTO>> getAll(@RequestParam Map<String, String> params) {
        Page<AdjustmentResponse> responses = adjustmentService.getAll(params);
        PageDTO pageDTO = new PageDTO(responses);
        ApiResponse<PageDTO> response = ApiResponse.<PageDTO>builder()
                .success(ErrorCode.SUCCESS)
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .message(Message.getAll("Adjustment"))
                .payload(pageDTO)
                .build();
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<AdjustmentResponse>> updateStatus(@PathVariable Long id, @RequestParam AdjustmentStatus status) {
       AdjustmentResponse adjustmentResponse = adjustmentService.updateStatus(id, status, "admin");
       ApiResponse<AdjustmentResponse> response = ApiResponse.<AdjustmentResponse>builder()
               .success(ErrorCode.SUCCESS)
               .status(HttpStatus.OK)
               .message(Message.updated("Adjustment status" , id))
               .payload(adjustmentResponse)
               .timestamp(LocalDateTime.now())
               .build();
       return  ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        adjustmentService.delete(id);
        return ApiResponse.<Void>builder()
                .success(ErrorCode.SUCCESS)
                .status(HttpStatus.OK)
                .message(Message.deleted("Adjustment", id))
                .timestamp(LocalDateTime.now())
                .build();
    }
}
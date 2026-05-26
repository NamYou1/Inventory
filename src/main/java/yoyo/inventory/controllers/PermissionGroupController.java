package yoyo.inventory.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import yoyo.inventory.common.Message;
import yoyo.inventory.constants.ErrorCode;
import yoyo.inventory.dto.request.PermissionGroupRequest;
import yoyo.inventory.dto.response.PermissionGroupResponse;
import yoyo.inventory.execption.ApiResponse;
import yoyo.inventory.services.PermissionGroupService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/permission-group")
@Tag(name = "Permission Group Controller", description = "APIs for managing permission groups")
public class PermissionGroupController {
    private final PermissionGroupService permissionGroupService;

    @GetMapping
    @PreAuthorize("hasAuthority('permission:read')")
    public ResponseEntity<ApiResponse<List<PermissionGroupResponse>>> getAll() {
        List<PermissionGroupResponse> payload = permissionGroupService.getAll();
        ApiResponse<List<PermissionGroupResponse>> response = ApiResponse.<List<PermissionGroupResponse>>builder()
                .success(ErrorCode.SUCCESS)
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .message(Message.getAll("PermissionGroup"))
                .payload(payload)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('permission:read')")
    public ResponseEntity<ApiResponse<PermissionGroupResponse>> getById(@PathVariable Long id) {
        PermissionGroupResponse payload = permissionGroupService.getById(id);
        ApiResponse<PermissionGroupResponse> response = ApiResponse.<PermissionGroupResponse>builder()
                .success(ErrorCode.SUCCESS)
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .message(Message.getById("PermissionGroup", id))
                .payload(payload)
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('permission:create')")
    public ResponseEntity<ApiResponse<PermissionGroupResponse>> create(@Valid @RequestBody PermissionGroupRequest request) {
        PermissionGroupResponse payload = permissionGroupService.create(request);
        ApiResponse<PermissionGroupResponse> response = ApiResponse.<PermissionGroupResponse>builder()
                .success(ErrorCode.SUCCESS)
                .status(HttpStatus.CREATED)
                .timestamp(LocalDateTime.now())
                .message(Message.created("PermissionGroup"))
                .payload(payload)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('permission:update')")
    public ResponseEntity<ApiResponse<PermissionGroupResponse>> update(@PathVariable Long id, @Valid @RequestBody PermissionGroupRequest request) {
        PermissionGroupResponse payload = permissionGroupService.update(id, request);
        ApiResponse<PermissionGroupResponse> response = ApiResponse.<PermissionGroupResponse>builder()
                .success(ErrorCode.SUCCESS)
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .message(Message.updated("PermissionGroup", id))
                .payload(payload)
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('permission:delete')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        permissionGroupService.delete(id);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(ErrorCode.SUCCESS)
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .message(Message.deleted("PermissionGroup", id))
                .build();
        return ResponseEntity.ok(response);
    }
}

package yoyo.inventory.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import yoyo.inventory.common.Message;
import yoyo.inventory.common.PageDTO;
import yoyo.inventory.constants.ErrorCode;
import yoyo.inventory.dto.response.StockResponse;
import yoyo.inventory.entities.User;
import yoyo.inventory.execption.ApiResponse;
import yoyo.inventory.repository.UserRepository;
import yoyo.inventory.services.StockService;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/stocks")
@RequiredArgsConstructor
@Tag(name = "Stock Management", description = "Endpoints for viewing and tracking current stock levels")
public class    StockController {

    private final StockService stockService;
    private final UserRepository userRepository;

    @GetMapping
    @PreAuthorize("hasAuthority('stock:read')")
    @Operation(summary = "Get stock records", description = "Returns a paginated list of stocks. Super admin sees all, store admin sees own store.")
    public ResponseEntity<ApiResponse<PageDTO>> getAll(@RequestParam Map<String, String> params) {
        // Determine current user and role
        User currentUser = getCurrentUser();
        boolean isSuperAdmin = currentUser.getRoles().stream().anyMatch(r -> "ROLE_SUPER_ADMIN".equals(r.getCode()));
        Page<StockResponse> responses;
        if (isSuperAdmin) {
            responses = stockService.getAll(params);
        } else {
            // Ensure user belongs to a store
            if (currentUser.getStore() == null) {
                throw new AccessDeniedException("User is not associated with any store.");
            }
            responses = stockService.getByStore(currentUser.getStore().getId(), params);
        }
        PageDTO pageDTO = new PageDTO(responses);
        
        ApiResponse<PageDTO> response = ApiResponse.<PageDTO>builder()
                .success(ErrorCode.SUCCESS)
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .message(Message.getAll("Stock"))
                .payload(pageDTO)
                .build();
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('stock:read')")
    @Operation(summary = "Get stock by ID", description = "Retrieves details of a specific stock record by its unique ID.")
    public ResponseEntity<ApiResponse<StockResponse>> getById(@PathVariable Long id) {
        StockResponse stockResponse = stockService.getById(id);
        
        ApiResponse<StockResponse> response = ApiResponse.<StockResponse>builder()
                .success(ErrorCode.SUCCESS)
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .message(Message.getById("Stock", id))
                .payload(stockResponse)
                .build();
                
        return ResponseEntity.ok(response);
    }

    @GetMapping("/product/{productId}/store/{storeId}")
    @PreAuthorize("hasAuthority('stock:read')")
    @Operation(summary = "Get stock balance by product and store", description = "Checks the active stock level of a specific product inside a specific store.")
    public ResponseEntity<ApiResponse<StockResponse>> getByProductAndStore(
            @PathVariable Long productId,
            @PathVariable Long storeId
    ) {
        User currentUser = getCurrentUser();
        boolean isSuperAdmin = currentUser.getRoles().stream().anyMatch(r -> "ROLE_SUPER_ADMIN".equals(r.getCode()));
        if (!isSuperAdmin) {
            if (currentUser.getStore() == null || !currentUser.getStore().getId().equals(storeId)) {
                throw new AccessDeniedException("You do not have permission to view stock for this store.");
            }
        }
        StockResponse stockResponse = stockService.getByProductAndStore(productId, storeId);
        
        ApiResponse<StockResponse> response = ApiResponse.<StockResponse>builder()
                .success(ErrorCode.SUCCESS)
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .message("Stock balance retrieved successfully for product and store")
                .payload(stockResponse)
                .build();
        
        return ResponseEntity.ok(response);
    }
    private User getCurrentUser() {
        String usernameOrEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() -> new org.springframework.security.core.userdetails.UsernameNotFoundException("Authenticated user not found"));
    }

}

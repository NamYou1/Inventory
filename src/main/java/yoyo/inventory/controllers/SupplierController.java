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

import yoyo.inventory.dto.request.SupplierRequest;

import yoyo.inventory.dto.response.SupplierResponse;
import yoyo.inventory.execption.ApiResponse;
import yoyo.inventory.services.SupplierService;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/suppliers")
@Tag(name = "Supplier Controller", description = "APIs for managing suppliers")
public class SupplierController {
    private  final SupplierService supplierService ;
    @GetMapping
    @PreAuthorize("hasAuthority('supplier:read')")
        public ResponseEntity<ApiResponse<PageDTO>> getAll(@RequestParam Map<String  , String> params){
        Page<SupplierResponse> responses = supplierService.getAllSuppliers(params);
        PageDTO pageDTO =  new PageDTO(responses);
        ApiResponse<PageDTO> response = ApiResponse.<PageDTO>builder()
                .success(ErrorCode.SUCCESS)
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .message(Message.getAll("Supplier"))
                .payload(pageDTO)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('supplier:read')")
    public  ResponseEntity<ApiResponse<SupplierResponse>> getById(@PathVariable Long id ){
        SupplierResponse exitsId = supplierService.getById(id);
        ApiResponse<SupplierResponse> response =ApiResponse.<SupplierResponse>builder()
                .success(ErrorCode.SUCCESS)
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .message(Message.getById("Supplier",id))
                .payload(exitsId)
                .build();
        return  ResponseEntity.ok(response);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('supplier:create')")
    public  ResponseEntity<ApiResponse<SupplierResponse>> create( @RequestBody SupplierRequest request){
        SupplierResponse supplier = supplierService.createSupplier(request);
        ApiResponse<SupplierResponse> response =ApiResponse.<SupplierResponse>builder()
                .success(ErrorCode.SUCCESS)
                .status(HttpStatus.CREATED)
                .timestamp(LocalDateTime.now())
                .message(Message.created("Supplier"))
                .payload(supplier)
                .build();
        return  ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('supplier:update')")
    public  ResponseEntity<ApiResponse<SupplierResponse>> update(@PathVariable Long id , @RequestBody SupplierRequest request){
        SupplierResponse supplier = supplierService.updateSupplier(id,request);
        ApiResponse<SupplierResponse> response =ApiResponse.<SupplierResponse>builder()
                .success(ErrorCode.SUCCESS)
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .message(Message.updated("Supplier",id))
                .payload(supplier)
                .build();
        return  ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('supplier:delete')")
    public ResponseEntity<ApiResponse<Void>> deleteSupplier(@PathVariable Long id) {
        supplierService.deleteSupplier(id);
        ApiResponse<Void>
                response = ApiResponse.<Void>builder()
                .success(ErrorCode.SUCCESS)
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .message(Message.deleted("Supplier", id))
                .build();
        return ResponseEntity.ok(response);
    }
}



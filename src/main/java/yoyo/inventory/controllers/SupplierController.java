package yoyo.inventory.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import yoyo.inventory.common.Message;
import yoyo.inventory.common.PageDTO;
import yoyo.inventory.constants.ErrorCode;

import yoyo.inventory.dto.request.SupplierRequest;

import yoyo.inventory.dto.response.SupplierResponse;
import yoyo.inventory.execption.ApiResponse;
import yoyo.inventory.services.SupplierService;

import java.time.Instant;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/suppliers")
@Tag(name = "Supplier Controller", description = "APIs for managing suppliers")
public class SupplierController {
    private  final SupplierService supplierService ;
    @GetMapping
    private ResponseEntity<ApiResponse<PageDTO>> getAll(@RequestParam Map<String  , String> params){
        Page<SupplierResponse> responses = supplierService.getAllSuppliers(params);
        PageDTO pageDTO =  new PageDTO(responses);
        ApiResponse<PageDTO> response = ApiResponse.<PageDTO>builder()
                .succeess(ErrorCode.SUCCESS)
                .status(HttpStatus.OK)
                .timestamp(Instant.now())
                .message(Message.getAll("Supplier"))
                .payload(pageDTO)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public  ResponseEntity<ApiResponse<SupplierResponse>> getById(@PathVariable Long id ){
        SupplierResponse exitsId = supplierService.getById(id);
        ApiResponse<SupplierResponse> response =ApiResponse.<SupplierResponse>builder()
                .succeess(ErrorCode.SUCCESS)
                .status(HttpStatus.OK)
                .timestamp(Instant.now())
                .message(Message.getById("Supplier",id))
                .payload(exitsId)
                .build();
        return  ResponseEntity.ok(response);
    }

    @PostMapping
    public  ResponseEntity<ApiResponse<SupplierResponse>> create( @RequestBody SupplierRequest request){
        SupplierResponse supplier = supplierService.createSupplier(request);
        ApiResponse<SupplierResponse> response =ApiResponse.<SupplierResponse>builder()
                .succeess(ErrorCode.SUCCESS)
                .status(HttpStatus.CREATED)
                .timestamp(Instant.now())
                .message(Message.created("Seller"))
                .payload(supplier)
                .build();
        return  ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public  ResponseEntity<ApiResponse<SupplierResponse>> update(@PathVariable Long id , @RequestBody SupplierRequest request){
        SupplierResponse supplier = supplierService.updateSupplier(id,request);
        ApiResponse<SupplierResponse> response =ApiResponse.<SupplierResponse>builder()
                .succeess(ErrorCode.SUCCESS)
                .status(HttpStatus.OK)
                .timestamp(Instant.now())
                .message(Message.updated("Seller",id))
                .payload(supplier)
                .build();
        return  ResponseEntity.ok(response);
    }

    @PostMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSeller(@PathVariable Long id) {
        supplierService.deleteSupplier(id);
        ApiResponse<Void>
                response = ApiResponse.<Void>builder()
                .succeess(ErrorCode.SUCCESS)
                .status(HttpStatus.OK)
                .timestamp(Instant.now())
                .message(Message.deleted("Seller", id))
                .build();
        return ResponseEntity.ok(response);
    }
}

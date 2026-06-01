package yoyo.inventory.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import yoyo.inventory.common.Message;
import yoyo.inventory.common.PageDTO;
import yoyo.inventory.constants.ErrorCode;
import yoyo.inventory.dto.request.ProductRequest;
import yoyo.inventory.dto.response.ProductImportResult;
import yoyo.inventory.dto.response.ProductResponse;
import yoyo.inventory.execption.ApiResponse;
import yoyo.inventory.services.ProductService;

import java.time.LocalDateTime;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/product")
@Tag(name = "Product", description = "Endpoints for managing products")
public class ProductController {
    private  final ProductService productService ;

    @GetMapping
    @PreAuthorize("hasAuthority('product:read')")
        public ResponseEntity<ApiResponse<PageDTO>> getAll(@RequestParam Map<String  , String> params){
        Page<ProductResponse> responses = productService.getAll(params);
        PageDTO pageDTO =  new PageDTO(responses);
        ApiResponse<PageDTO> response = ApiResponse.<PageDTO>builder()
                .success(ErrorCode.SUCCESS)
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .message(Message.getAll("Product"))
                .payload(pageDTO)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('product:read')")
    public  ResponseEntity<ApiResponse<ProductResponse>> getById(@PathVariable Long id ){
        ProductResponse exitsId = productService.getById(id);
        ApiResponse<ProductResponse> response =ApiResponse.<ProductResponse>builder()
                .success(ErrorCode.SUCCESS)
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .message(Message.getById("Supplier",id))
                .payload(exitsId)
                .build();
        return  ResponseEntity.ok(response);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('product:create')")
    public ResponseEntity<ApiResponse<ProductResponse>> create(
            @ModelAttribute ProductRequest request
    ) {
        ProductResponse product = productService.create(request , request.getImageUrl());
        ApiResponse<ProductResponse> response =ApiResponse.<ProductResponse>builder()
                .success(ErrorCode.SUCCESS)
                .status(HttpStatus.CREATED)
                .timestamp(LocalDateTime.now())
                .message(Message.created("Product"))
                .payload(product)
                .build();
        return  ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('product:update')")
    public  ResponseEntity<ApiResponse<ProductResponse>> update(@PathVariable Long id , @ModelAttribute ProductRequest request){
        ProductResponse supplier = productService.update(id,request);
        ApiResponse<ProductResponse> response =ApiResponse.<ProductResponse>builder()
                .success(ErrorCode.SUCCESS)
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .message(Message.updated("Product",id))
                .payload(supplier)
                .build();
        return  ResponseEntity.ok(response);
    }

    @PostMapping("/{id}")
    @PreAuthorize("hasAuthority('product:delete')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        productService.delete(id);
        ApiResponse<Void>
                response = ApiResponse.<Void>builder()
                .success(ErrorCode.SUCCESS)
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .message(Message.deleted("Product", id))
                .build();
        return ResponseEntity.ok(response);
    }

    // ========================= Excel Import =========================

    @PostMapping(value = "/import-excel", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('product:create')")
    @Operation(summary = "Import products from Excel (.xlsx)")
    public ResponseEntity<ApiResponse<ProductImportResult>> importExcel(
            @RequestParam("file") MultipartFile file) {
        ProductImportResult result = productService.importFromExcel(file);
        ApiResponse<ProductImportResult> response = ApiResponse.<ProductImportResult>builder()
                .success(ErrorCode.SUCCESS)
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .message("Imported " + result.getSuccessCount() + " of " + result.getTotalRows() + " products")
                .payload(result)
                .build();
        return ResponseEntity.ok(response);
    }

    // ========================= Excel Export =========================

    @GetMapping("/export-excel")
    @PreAuthorize("hasAuthority('product:read')")
    @Operation(summary = "Export all products to Excel (.xlsx)")
    public ResponseEntity<byte[]> exportExcel() {
        byte[] excelBytes = productService.exportToExcel();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", "products.xlsx");
        headers.setContentLength(excelBytes.length);
        return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);
    }
}

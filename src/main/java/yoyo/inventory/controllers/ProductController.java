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
import yoyo.inventory.dto.request.ProductRequest;
import yoyo.inventory.dto.response.ProductResponse;
import yoyo.inventory.execption.ApiResponse;
import yoyo.inventory.services.ProductService;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/product")
@Tag(name = "Product", description = "Endpoints for managing products")
public class ProductController {
    private  final ProductService productService ;

    @GetMapping
    private ResponseEntity<ApiResponse<PageDTO>> getAll(@RequestParam Map<String  , String> params){
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

    @PostMapping
    public  ResponseEntity<ApiResponse<ProductResponse>> create( @RequestBody ProductRequest request){
        ProductResponse product = productService.create(request);
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
    public  ResponseEntity<ApiResponse<ProductResponse>> update(@PathVariable Long id , @RequestBody ProductRequest request){
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
}

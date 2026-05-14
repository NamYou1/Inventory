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
import yoyo.inventory.dto.request.CategoryRequest;
import yoyo.inventory.dto.response.CategoryResponse;
import yoyo.inventory.execption.ApiResponse;
import yoyo.inventory.services.CategoryService;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/category")
@Tag(name = "Category Controller", description = "APIs for managing product categories")
public class CategoryController {
    private  final CategoryService categoryService ;


    @GetMapping
    private ResponseEntity<ApiResponse<PageDTO>> getAll(@RequestParam Map<String  , String> params){
        Page<CategoryResponse> responses = categoryService.getAll(params);
        PageDTO pageDTO =  new PageDTO(responses);
        ApiResponse<PageDTO> response = ApiResponse.<PageDTO>builder()
                .success(ErrorCode.SUCCESS)
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .message(Message.getAll("Category"))
                .payload(pageDTO)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public  ResponseEntity<ApiResponse<CategoryResponse>> getById(@PathVariable Long id ){
        CategoryResponse exitsId = categoryService.getById(id);
        ApiResponse<CategoryResponse> response =ApiResponse.<CategoryResponse>builder()
                .success(ErrorCode.SUCCESS)
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .message(Message.getById("Supplier",id))
                .payload(exitsId)
                .build();
        return  ResponseEntity.ok(response);
    }

    @PostMapping
    public  ResponseEntity<ApiResponse<CategoryResponse>> create( @RequestBody CategoryRequest request){
        CategoryResponse category = categoryService.createCategory(request);
        ApiResponse<CategoryResponse> response =ApiResponse.<CategoryResponse>builder()
                .success(ErrorCode.SUCCESS)
                .status(HttpStatus.CREATED)
                .timestamp(LocalDateTime.now())
                .message(Message.created("Category"))
                .payload(category)
                .build();
        return  ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public  ResponseEntity<ApiResponse<CategoryResponse>> update(@PathVariable Long id , @RequestBody CategoryRequest request){
        CategoryResponse supplier = categoryService.updateCategory(id,request);
        ApiResponse<CategoryResponse> response =ApiResponse.<CategoryResponse>builder()
                .success(ErrorCode.SUCCESS)
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .message(Message.updated("Category",id))
                .payload(supplier)
                .build();
        return  ResponseEntity.ok(response);
    }

    @PostMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        ApiResponse<Void>
                response = ApiResponse.<Void>builder()
                .success(ErrorCode.SUCCESS)
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .message(Message.deleted("Category", id))
                .build();
        return ResponseEntity.ok(response);
    }
}

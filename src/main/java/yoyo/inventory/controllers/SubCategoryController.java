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
import yoyo.inventory.dto.request.SubCategoryRequest;
import yoyo.inventory.dto.response.SubCategoryResponse;
import yoyo.inventory.execption.ApiResponse;
import yoyo.inventory.services.SubCategoryService;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Tag(name = "SubCategory", description = "APIs for managing subcategories in the inventory system")
@RequestMapping("/api/v1/subcategory")
public class SubCategoryController {
    private  final SubCategoryService subCategoryService ;


    @GetMapping
    @PreAuthorize("hasAuthority('subcategory:read')")
        public ResponseEntity<ApiResponse<PageDTO>> getAll(@RequestParam Map<String  , String> params){
        Page<SubCategoryResponse> responses = subCategoryService.getAll(params);
        PageDTO pageDTO =  new PageDTO(responses);
        ApiResponse<PageDTO> response = ApiResponse.<PageDTO>builder()
                .success(ErrorCode.SUCCESS)
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .message(Message.getAll("SubCategory"))
                .payload(pageDTO)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('subcategory:read')")
    public  ResponseEntity<ApiResponse<SubCategoryResponse>> getById(@PathVariable Long id ){
        SubCategoryResponse exitsId = subCategoryService.getById(id);
        ApiResponse<SubCategoryResponse> response =ApiResponse.<SubCategoryResponse>builder()
                .success(ErrorCode.SUCCESS)
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .message(Message.getById("SubCategory",id))
                .payload(exitsId)
                .build();
        return  ResponseEntity.ok(response);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('subcategory:create')")
    public  ResponseEntity<ApiResponse<SubCategoryResponse>> create( @RequestBody SubCategoryRequest request){
        SubCategoryResponse category = subCategoryService.create(request);
        ApiResponse<SubCategoryResponse> response =ApiResponse.<SubCategoryResponse>builder()
                .success(ErrorCode.SUCCESS)
                .status(HttpStatus.CREATED)
                .timestamp(LocalDateTime.now())
                .message(Message.created("SubCategory"))
                .payload(category)
                .build();
        return  ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('subcategory:update')")
    public  ResponseEntity<ApiResponse<SubCategoryResponse>> update(@PathVariable Long id , @RequestBody SubCategoryRequest request){
        SubCategoryResponse subCategoryResponse = subCategoryService.update(id,request);
        ApiResponse<SubCategoryResponse> response =ApiResponse.<SubCategoryResponse>builder()
                .success(ErrorCode.SUCCESS)
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .message(Message.updated("SubCategory",id))
                .payload(subCategoryResponse)
                .build();
        return  ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('subcategory:delete')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        subCategoryService.delete(id);
        ApiResponse<Void>
                response = ApiResponse.<Void>builder()
                .success(ErrorCode.SUCCESS)
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .message(Message.deleted("SubCategory", id))
                .build();
        return ResponseEntity.ok(response);
    }
}



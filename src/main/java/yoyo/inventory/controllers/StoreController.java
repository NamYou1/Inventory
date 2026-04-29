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
import yoyo.inventory.dto.request.StoreRequest;
import yoyo.inventory.dto.response.StoreResponse;
import yoyo.inventory.execption.ApiResponse;
import yoyo.inventory.services.StoreService;

import java.time.Instant;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/store")
@Tag(name = "Store Management", description = "APIs for managing stores")
public class StoreController {
    private  final StoreService storeService ;

    @GetMapping
    private ResponseEntity<ApiResponse<PageDTO>> getAll(@RequestParam Map<String  , String> params){
        Page<StoreResponse> responses = storeService.getAll(params);
        PageDTO pageDTO =  new PageDTO(responses);
        ApiResponse<PageDTO> response = ApiResponse.<PageDTO>builder()
                .succeess(ErrorCode.SUCCESS)
                .status(HttpStatus.OK)
                .timestamp(Instant.now())
                .message(Message.getAll("Stores"))
                .payload(pageDTO)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public  ResponseEntity<ApiResponse<StoreResponse>> getById(@PathVariable Long id ){
        StoreResponse exitsId = storeService.getById(id);
        ApiResponse<StoreResponse> response =ApiResponse.<StoreResponse>builder()
                .succeess(ErrorCode.SUCCESS)
                .status(HttpStatus.OK)
                .timestamp(Instant.now())
                .message(Message.getById("Stores",id))
                .payload(exitsId)
                .build();
        return  ResponseEntity.ok(response);
    }

    @PostMapping
    public  ResponseEntity<ApiResponse<StoreResponse>> create( @RequestBody StoreRequest request){
        StoreResponse category = storeService.create(request);
        ApiResponse<StoreResponse> response =ApiResponse.<StoreResponse>builder()
                .succeess(ErrorCode.SUCCESS)
                .status(HttpStatus.CREATED)
                .timestamp(Instant.now())
                .message(Message.created("Stores"))
                .payload(category)
                .build();
        return  ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public  ResponseEntity<ApiResponse<StoreResponse>> update(@PathVariable Long id , @RequestBody StoreRequest request){
        StoreResponse store = storeService.update(id,request);
        ApiResponse<StoreResponse> response =ApiResponse.<StoreResponse>builder()
                .succeess(ErrorCode.SUCCESS)
                .status(HttpStatus.OK)
                .timestamp(Instant.now())
                .message(Message.updated("Stores",id))
                .payload(store)
                .build();
        return  ResponseEntity.ok(response);
    }

    @PostMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        storeService.delete(id);
        ApiResponse<Void>
                response = ApiResponse.<Void>builder()
                .succeess(ErrorCode.SUCCESS)
                .status(HttpStatus.OK)
                .timestamp(Instant.now())
                .message(Message.deleted("Store", id))
                .build();
        return ResponseEntity.ok(response);
    }
}

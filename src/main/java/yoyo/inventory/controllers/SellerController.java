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
import yoyo.inventory.dto.request.SellerRequest;
import yoyo.inventory.dto.response.SellerResponse;
import yoyo.inventory.execption.ApiResponse;
import yoyo.inventory.services.SellerService;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/seller")
@Tag(name = "Seller", description = "Endpoints for managing sellers")
public class SellerController {
    private  final SellerService sellerService ;

    @GetMapping
    @PreAuthorize("hasAuthority('seller:read')")
        public ResponseEntity<ApiResponse<PageDTO>> getAll(@RequestParam Map<String  , String> params){
        Page<SellerResponse> sellerResponses = sellerService.getAll(params);
        PageDTO pageDTO =  new PageDTO(sellerResponses);
        ApiResponse<PageDTO> response = ApiResponse.<PageDTO>builder()
                .success(ErrorCode.SUCCESS)
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .message(Message.getAll("Seller"))
                .payload(pageDTO)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('seller:read')")
    public  ResponseEntity<ApiResponse<SellerResponse>> getById(@PathVariable Long id ){
        SellerResponse sellerResponse = sellerService.getById(id);
        ApiResponse<SellerResponse> response =ApiResponse.<SellerResponse>builder()
                .success(ErrorCode.SUCCESS)
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .message(Message.getById("Seller",id))
                .payload(sellerResponse)
                .build();
        return  ResponseEntity.ok(response);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('seller:create')")
    public  ResponseEntity<ApiResponse<SellerResponse>> create( @RequestBody SellerRequest request){
        SellerResponse sellerResponse = sellerService.createSeller(request);
        ApiResponse<SellerResponse> response =ApiResponse.<SellerResponse>builder()
                .success(ErrorCode.SUCCESS)
                .status(HttpStatus.CREATED)
                .timestamp(LocalDateTime.now())
                .message(Message.created("Seller"))
                .payload(sellerResponse)
                .build();
        return  ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('seller:update')")
    public  ResponseEntity<ApiResponse<SellerResponse>> update(@PathVariable Long id , @RequestBody SellerRequest request){
        SellerResponse sellerResponse = sellerService.updateSeller(id,request);
        ApiResponse<SellerResponse> response =ApiResponse.<SellerResponse>builder()
                .success(ErrorCode.SUCCESS)
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .message(Message.updated("Seller",id))
                .payload(sellerResponse)
                .build();
        return  ResponseEntity.ok(response);
    }

    @PostMapping("/{id}")
    @PreAuthorize("hasAuthority('seller:delete')")
    public ResponseEntity<ApiResponse<Void>> deleteSeller(@PathVariable Long id) {
        sellerService.deleteSeller(id);
        ApiResponse<Void>
                response = ApiResponse.<Void>builder()
                .success(ErrorCode.SUCCESS)
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .message(Message.deleted("Seller", id))
                .build();
        return ResponseEntity.ok(response);
    }



}



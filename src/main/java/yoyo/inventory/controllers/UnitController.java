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
import yoyo.inventory.dto.request.UnitRequest;
import yoyo.inventory.dto.response.UnitResponse;
import yoyo.inventory.execption.ApiResponse;
import yoyo.inventory.services.UnitService;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/unit")
@Tag(name = "Unit Management", description = "APIs for managing units of measurement")
public class UnitController {
    private  final UnitService unitService ;
    @GetMapping
    @PreAuthorize("hasAuthority('unit:read')")
        public ResponseEntity<ApiResponse<PageDTO>> getAll(@RequestParam Map<String  , String> params){
        Page<UnitResponse> responses = unitService.getAll(params);
        PageDTO pageDTO =  new PageDTO(responses);
        ApiResponse<PageDTO> response = ApiResponse.<PageDTO>builder()
                .success(ErrorCode.SUCCESS)
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .message(Message.getAll("Unit"))
                .payload(pageDTO)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('unit:read')")
    public  ResponseEntity<ApiResponse<UnitResponse>> getById(@PathVariable Long id ){
        UnitResponse exitsId = unitService.getById(id);
        ApiResponse<UnitResponse> response =ApiResponse.<UnitResponse>builder()
                .success(ErrorCode.SUCCESS)
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .message(Message.getById("Unit",id))
                .payload(exitsId)
                .build();
        return  ResponseEntity.ok(response);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('unit:create')")
    public  ResponseEntity<ApiResponse<UnitResponse>> create( @RequestBody UnitRequest request){
        UnitResponse unitResponse = unitService.create(request);
        ApiResponse<UnitResponse> response =ApiResponse.<UnitResponse>builder()
                .success(ErrorCode.SUCCESS)
                .status(HttpStatus.CREATED)
                .timestamp(LocalDateTime.now())
                .message(Message.created("SubCategory"))
                .payload(unitResponse)
                .build();
        return  ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('unit:update')")
    public  ResponseEntity<ApiResponse<UnitResponse>> update(@PathVariable Long id , @RequestBody UnitRequest request){
        UnitResponse unitResponse = unitService.update(id,request);
        ApiResponse<UnitResponse> response =ApiResponse.<UnitResponse>builder()
                .success(ErrorCode.SUCCESS)
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .message(Message.updated("Unit",id))
                .payload(unitResponse)
                .build();
        return  ResponseEntity.ok(response);
    }

    @PostMapping("/{id}")
    @PreAuthorize("hasAuthority('unit:delete')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        unitService.delete(id);
        ApiResponse<Void>
                response = ApiResponse.<Void>builder()
                .success(ErrorCode.SUCCESS)
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .message(Message.deleted("Unit", id))
                .build();
        return ResponseEntity.ok(response);
    }
}



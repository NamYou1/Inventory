package yoyo.inventory.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import yoyo.inventory.constants.ErrorCode;
import yoyo.inventory.execption.ApiResponse;
import yoyo.inventory.services.FileStorageService;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/upload")
@RequiredArgsConstructor
@Tag(name = "Upload", description = "Endpoints for uploading files to local RustFS object storage")
public class UploadController {

    private final FileStorageService fileStorageService;

    @PostMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Upload an image to RustFS", description = "Accepts an image file and returns the file URL from local RustFS S3-compatible storage.")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "folder", defaultValue = "inventory") String folder
    ) {
        String secureUrl = fileStorageService.uploadFile(file, folder);

        Map<String, String> payload = new HashMap<>();
        payload.put("url", secureUrl);
        payload.put("folder", folder);

        ApiResponse<Map<String, String>> response = ApiResponse.<Map<String, String>>builder()
                .success(ErrorCode.SUCCESS)
                .status(HttpStatus.CREATED)
                .message("Image uploaded successfully to RustFS")
                .payload(payload)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}

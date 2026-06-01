package yoyo.inventory.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import yoyo.inventory.dto.request.UserProfileRequest;
import yoyo.inventory.dto.response.UserProfileResponse;
import yoyo.inventory.execption.ApiResponse;
import yoyo.inventory.services.UserProfileService;

import java.security.Principal;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
public class UserProfileController {
    private final UserProfileService userProfileService;

    @GetMapping
    public ApiResponse<UserProfileResponse> getProfile(Principal principal) {
        if (principal == null) {
            return ApiResponse.<UserProfileResponse>builder()
                    .success("false")
                    .status(HttpStatus.UNAUTHORIZED)
                    .message("User is not authenticated")
                    .timestamp(LocalDateTime.now())
                    .build();
        }

        return ApiResponse.<UserProfileResponse>builder()
                .success("true")
                .status(HttpStatus.OK)
                .message("Profile fetched successfully")
                .payload(userProfileService.getProfile(principal.getName()))
                .timestamp(LocalDateTime.now())
                .build();
    }

    @PostMapping
    public ApiResponse<UserProfileResponse> createOrUpdateProfile(
            Principal principal,
            @Valid @RequestBody UserProfileRequest request
    ) {
        if (principal == null) {
            return ApiResponse.<UserProfileResponse>builder()
                    .success("false")
                    .status(HttpStatus.UNAUTHORIZED)
                    .message("User is not authenticated")
                    .timestamp(LocalDateTime.now())
                    .build();
        }

        return ApiResponse.<UserProfileResponse>builder()
                .success("true")
                .status(HttpStatus.OK)
                .message("Profile updated successfully")
                .payload(userProfileService.createOrUpdateProfile(principal.getName(), request))
                .timestamp(LocalDateTime.now())
                .build();
    }
}

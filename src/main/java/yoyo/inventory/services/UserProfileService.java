package yoyo.inventory.services;

import yoyo.inventory.dto.request.UserProfileRequest;
import yoyo.inventory.dto.response.UserProfileResponse;

public interface UserProfileService {
    UserProfileResponse getProfile(String email);
    UserProfileResponse createOrUpdateProfile(String email, UserProfileRequest request);
}

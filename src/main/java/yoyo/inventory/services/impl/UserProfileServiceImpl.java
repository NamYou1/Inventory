package yoyo.inventory.services.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import yoyo.inventory.dto.request.UserProfileRequest;
import yoyo.inventory.dto.response.UserProfileResponse;
import yoyo.inventory.entities.User;
import yoyo.inventory.entities.UserProfile;
import yoyo.inventory.execption.ApiException;
import yoyo.inventory.repository.UserRepository;
import yoyo.inventory.repository.UserProfileRepository;
import yoyo.inventory.services.UserProfileService;

@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;

    @Override
    @Transactional
    public UserProfileResponse getProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));

        UserProfile profile = userProfileRepository.findByUser(user)
                .orElseGet(() -> {
                    UserProfile empty = new UserProfile();
                    empty.setUser(user);
                    return empty;
                });

        return mapToResponse(user, profile);
    }

    @Override
    @Transactional
    public UserProfileResponse createOrUpdateProfile(String email, UserProfileRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));

        UserProfile profile = userProfileRepository.findByUser(user)
                .orElseGet(() -> {
                    UserProfile newProfile = new UserProfile();
                    newProfile.setUser(user);
                    return newProfile;
                });

        profile.setBio(request.getBio());
        profile.setGender(request.getGender());
        profile.setDateOfBirth(request.getDateOfBirth());
        profile.setAddress(request.getAddress());
        profile.setGithubUrl(request.getGithubUrl());
        profile.setLinkedinUrl(request.getLinkedinUrl());
        profile.setWebsiteUrl(request.getWebsiteUrl());

        UserProfile saved = userProfileRepository.save(profile);
        return mapToResponse(user, saved);
    }

    private UserProfileResponse mapToResponse(User user, UserProfile profile) {
        return UserProfileResponse.builder()
                .id(profile.getId())
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(user.getPhone())
                .avatarUrl(user.getAvatarUrl())
                .bio(profile.getBio())
                .gender(profile.getGender())
                .dateOfBirth(profile.getDateOfBirth())
                .address(profile.getAddress())
                .githubUrl(profile.getGithubUrl())
                .linkedinUrl(profile.getLinkedinUrl())
                .websiteUrl(profile.getWebsiteUrl())
                .createdAt(profile.getCreatedAt())
                .updatedAt(profile.getUpdatedAt())
                .build();
    }
}

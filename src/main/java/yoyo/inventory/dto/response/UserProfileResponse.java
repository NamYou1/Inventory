package yoyo.inventory.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class UserProfileResponse {
    private Long id;
    private Long userId;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private String avatarUrl;
    private String bio;
    private String gender;
    private LocalDate dateOfBirth;
    private String address;
    private String githubUrl;
    private String linkedinUrl;
    private String websiteUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

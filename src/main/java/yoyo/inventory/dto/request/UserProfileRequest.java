package yoyo.inventory.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UserProfileRequest {
    private String bio;
    private String gender;
    private LocalDate dateOfBirth;
    private String address;
    private String githubUrl;
    private String linkedinUrl;
    private String websiteUrl;
}

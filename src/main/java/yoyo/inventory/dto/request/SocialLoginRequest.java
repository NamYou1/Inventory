package yoyo.inventory.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SocialLoginRequest {
    @NotBlank(message = "Provider is required")
    private String provider; // e.g. "GOOGLE", "GITHUB", "FACEBOOK"

    @NotBlank(message = "Token is required")
    private String token;

    private String email;
    private String firstName;
    private String lastName;
    private String avatarUrl;
    private String providerId;
}

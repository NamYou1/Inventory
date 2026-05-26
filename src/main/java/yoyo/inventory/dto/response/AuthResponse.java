package yoyo.inventory.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private Long expiresIn;
    private Long userId;
    private String username;
    private String email;
    private java.util.List<String> roles;
    private java.util.List<String> permissions;
    private Long storeId;
}

package yoyo.inventory.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.Set;

@Data
public class UserRequest {
    @NotBlank
    private String username;
    
    @NotBlank
    @Email
    private String email;
    
    private String password; // Optional on update
    private String firstName;
    private String lastName;
    private String phone;
    private Boolean isActive = true;
    private Long storeId;
    private Set<String> roleCodes;
}

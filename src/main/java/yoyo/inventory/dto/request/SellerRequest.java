package yoyo.inventory.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import yoyo.inventory.entities.status.Status;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SellerRequest {
    @NotNull(message = "Seller name is required")
    private  String name ;
    @NotNull(message = "Seller email is required")
    @NotBlank(message = "Seller email cannot be blank")
    private  String email ;
    @NotNull(message = "Seller phone number is required")
    private  String phone ;
    private Status status = Status.ACTIVE;
}

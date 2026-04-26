package yoyo.inventory.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SupplierRequest {
    @NotNull(message = "Supplier name is required")
    private  String name ;
    private  String email ;
    @NotNull(message = "Supplier phone number is required")
    private  String phone ;
    private  String address ;
    private  String status  = "ACTIVE";

}

package yoyo.inventory.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import yoyo.inventory.entities.status.Status;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SupplierRequest {
    @NotNull(message = "Supplier name is required")
    private  String name ;
    private  String email ;
    @NotNull(message = "Supplier phone number is required")
    private  String phone ;
    private  String address ;
    private Status status  = Status.ACTIVE;

}

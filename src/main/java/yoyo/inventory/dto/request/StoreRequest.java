package yoyo.inventory.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import yoyo.inventory.entities.status.Status;

@Data
public class StoreRequest {
    @NotNull(message = "Name is required")
    private  String name;
    @NotNull(message = "Code is required")
    private  String code ;
    private  String logo ;
    @NotNull(message = "Email is required")
    private  String email;
    @NotNull(message = "Phone is required")
    private  String phone ;
    private  String addressOne ;
    private  String addressTwo;
    private  String city ;
    private  String state ;
    private  String  postalCode ;
    private  String  country ;
    private  String receiptHeader ;
    private  String receiptFooter ;
    private Status status = Status.ACTIVE;
}

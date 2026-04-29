package yoyo.inventory.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import yoyo.inventory.entities.status.Status;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubCategoryRequest {
    @NotNull(message = "name is required")
    private  String name ;
    @NotNull(message = "code is required")
    private  String code ;
    private Status status = Status.ACTIVE;
    @NotNull(message = "categoryId is required")
    private  Long categoryId ;
}

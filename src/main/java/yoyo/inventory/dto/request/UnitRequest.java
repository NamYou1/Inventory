package yoyo.inventory.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.NumberFormat;
import yoyo.inventory.entities.status.Status;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UnitRequest {
        private  Integer baseUnit ;
        @NotNull(message = "Code is required ")
        private  String code ;
        @NotNull(message = "Name is required ")
        private  String name;
        private  String operation ;
        @NumberFormat
        private  Integer operationValue ;
        private Status status = Status.ACTIVE;
}

package yoyo.inventory.specification.suppliers;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import yoyo.inventory.entities.Suppliers;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SupplierFilter {
    private  String name ;
    private  String email ;

}

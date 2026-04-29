package yoyo.inventory.specification.sellers;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SellerFilter {
    private String name ;
    private String email ;
    private String phone ;
}

package yoyo.inventory.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PermissionGroupResponse {
    private Long id;
    private String code;
    private String name;
    private String description;
}

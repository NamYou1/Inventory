package yoyo.inventory.services;

import yoyo.inventory.dto.request.RoleRequest;
import yoyo.inventory.dto.response.RoleResponse;
import yoyo.inventory.entities.Role;

import java.util.List;

public interface RoleService {
    List<RoleResponse> getAll();
    Role findById(Long id);
    RoleResponse getById(Long id);
    RoleResponse create(RoleRequest request);
    RoleResponse update(Long id, RoleRequest request);
    void delete(Long id);
}

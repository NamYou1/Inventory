package yoyo.inventory.services;

import yoyo.inventory.dto.request.PermissionGroupRequest;
import yoyo.inventory.dto.response.PermissionGroupResponse;
import yoyo.inventory.entities.PermissionGroup;

import java.util.List;

public interface PermissionGroupService {
    List<PermissionGroupResponse> getAll();
    PermissionGroup findById(Long id);
    PermissionGroupResponse getById(Long id);
    PermissionGroupResponse create(PermissionGroupRequest request);
    PermissionGroupResponse update(Long id, PermissionGroupRequest request);
    void delete(Long id);
}

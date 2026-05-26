package yoyo.inventory.services;

import yoyo.inventory.dto.request.PermissionRequest;
import yoyo.inventory.dto.response.PermissionResponse;
import yoyo.inventory.entities.Permission;

import java.util.List;

public interface PermissionService {
    List<PermissionResponse> getAll();
    Permission findById(Long id);
    PermissionResponse getById(Long id);
    PermissionResponse create(PermissionRequest request);
    PermissionResponse update(Long id, PermissionRequest request);
    void delete(Long id);
}

package yoyo.inventory.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import yoyo.inventory.common.UniqueChecker;
import yoyo.inventory.dto.request.PermissionRequest;
import yoyo.inventory.dto.response.PermissionResponse;
import yoyo.inventory.entities.Permission;
import yoyo.inventory.entities.PermissionGroup;
import yoyo.inventory.execption.ResourceNotFoundException;
import yoyo.inventory.repository.PermissionRepository;
import yoyo.inventory.services.PermissionGroupService;
import yoyo.inventory.services.PermissionService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {
    private final PermissionRepository permissionRepository;
    private final PermissionGroupService permissionGroupService;
    private final UniqueChecker uniqueChecker;

    @Override
    public List<PermissionResponse> getAll() {
        return permissionRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    public Permission findById(Long id) {
        return permissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Permission", id));
    }

    @Override
    public PermissionResponse getById(Long id) {
        return toResponse(findById(id));
    }

    @Override
    public PermissionResponse create(PermissionRequest request) {
        PermissionGroup group = permissionGroupService.findById(request.getGroupId());
        Permission permission = new Permission();
        permission.setCode(request.getCode());
        permission.setName(request.getName());
        permission.setDescription(request.getDescription());
        permission.setGroup(group);
        uniqueChecker.verify(permissionRepository, permission, "code", permission.getCode());
        Permission saved = permissionRepository.save(permission);
        return toResponse(saved);
    }

    @Override
    public PermissionResponse update(Long id, PermissionRequest request) {
        Permission permission = findById(id);
        PermissionGroup group = permissionGroupService.findById(request.getGroupId());
        permission.setCode(request.getCode());
        permission.setName(request.getName());
        permission.setDescription(request.getDescription());
        permission.setGroup(group);
        uniqueChecker.verify(permissionRepository, permission, "code", permission.getCode());
        Permission saved = permissionRepository.save(permission);
        return toResponse(saved);
    }

    @Override
    public void delete(Long id) {
        Permission permission = findById(id);
        permissionRepository.delete(permission);
    }

    private PermissionResponse toResponse(Permission permission) {
        return new PermissionResponse(
                permission.getId(),
                permission.getCode(),
                permission.getName(),
                permission.getDescription(),
                permission.getGroup().getId(),
                permission.getGroup().getCode(),
                permission.getGroup().getName()
        );
    }
}

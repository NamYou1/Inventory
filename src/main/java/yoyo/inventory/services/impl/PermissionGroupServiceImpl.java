package yoyo.inventory.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import yoyo.inventory.common.UniqueChecker;
import yoyo.inventory.dto.request.PermissionGroupRequest;
import yoyo.inventory.dto.response.PermissionGroupResponse;
import yoyo.inventory.entities.PermissionGroup;
import yoyo.inventory.execption.ResourceNotFoundException;
import yoyo.inventory.repository.PermissionGroupRepository;
import yoyo.inventory.services.PermissionGroupService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PermissionGroupServiceImpl implements PermissionGroupService {
    private final PermissionGroupRepository permissionGroupRepository;
    private final UniqueChecker uniqueChecker;

    @Override
    public List<PermissionGroupResponse> getAll() {
        return permissionGroupRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    public PermissionGroup findById(Long id) {
        return permissionGroupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PermissionGroup", id));
    }

    @Override
    public PermissionGroupResponse getById(Long id) {
        return toResponse(findById(id));
    }

    @Override
    public PermissionGroupResponse create(PermissionGroupRequest request) {
        PermissionGroup group = new PermissionGroup();
        group.setCode(request.getCode());
        group.setName(request.getName());
        group.setDescription(request.getDescription());
        uniqueChecker.verify(permissionGroupRepository, group, "code", group.getCode());
        PermissionGroup saved = permissionGroupRepository.save(group);
        return toResponse(saved);
    }

    @Override
    public PermissionGroupResponse update(Long id, PermissionGroupRequest request) {
        PermissionGroup group = findById(id);
        group.setCode(request.getCode());
        group.setName(request.getName());
        group.setDescription(request.getDescription());
        uniqueChecker.verify(permissionGroupRepository, group, "code", group.getCode());
        PermissionGroup saved = permissionGroupRepository.save(group);
        return toResponse(saved);
    }

    @Override
    public void delete(Long id) {
        PermissionGroup group = findById(id);
        permissionGroupRepository.delete(group);
    }

    private PermissionGroupResponse toResponse(PermissionGroup group) {
        return new PermissionGroupResponse(group.getId(), group.getCode(), group.getName(), group.getDescription());
    }
}

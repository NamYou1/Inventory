package yoyo.inventory.services.impl;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import yoyo.inventory.common.UniqueChecker;
import yoyo.inventory.dto.request.RoleRequest;
import yoyo.inventory.dto.response.RoleResponse;
import yoyo.inventory.entities.Permission;
import yoyo.inventory.entities.Role;
import yoyo.inventory.execption.ResourceNotFoundException;
import yoyo.inventory.repository.RoleRepository;
import yoyo.inventory.services.PermissionService;
import yoyo.inventory.services.RoleService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;
    private final PermissionService permissionService;
    private final UniqueChecker uniqueChecker;

    @Override
    public List<RoleResponse> getAll() {
        return roleRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    public Role findById(Long id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role", id));
    }

    @Override
    public RoleResponse getById(Long id) {
        return toResponse(findById(id));
    }

    @Override
    public RoleResponse create(RoleRequest request) {
        Role role = new Role();
        role.setCode(request.getCode());
        role.setName(request.getName());
        role.setDescription(request.getDescription());
        role.setPermissions(resolvePermissions(request.getPermissionIds()));
        uniqueChecker.verify(roleRepository, role, "code", role.getCode());
        Role saved = roleRepository.save(role);
        return toResponse(saved);
    }

    @Override
    public RoleResponse update(Long id, RoleRequest request) {
        Role role = findById(id);
        String oldCode = role.getCode();

        role.setCode(request.getCode());
        role.setName(request.getName());
        role.setDescription(request.getDescription());
        role.setPermissions(resolvePermissions(request.getPermissionIds()));

        // Only check uniqueness if the code actually changed
        if (!oldCode.equals(request.getCode())) {
            uniqueChecker.verify(roleRepository, role, "code", role.getCode());
        }

        Role saved = roleRepository.save(role);
        return toResponse(saved);
    }

    @Override
    public void delete(Long id) {
        Role role = findById(id);
        roleRepository.delete(role);
    }

    private Set<Permission> resolvePermissions(Set<Long> permissionIds) {
        Set<Permission> permissions = new HashSet<>();
        if (permissionIds == null || permissionIds.isEmpty()) {
            return permissions;
        }
        for (Long permissionId : permissionIds) {
            permissions.add(permissionService.findById(permissionId));
        }
        return permissions;
    }

    private RoleResponse toResponse(Role role) {
        Set<Long> permissionIds = role.getPermissions().stream().map(Permission::getId).collect(java.util.stream.Collectors.toSet());
        return new RoleResponse(role.getId(), role.getCode(), role.getName(), role.getDescription(), permissionIds);
    }
}

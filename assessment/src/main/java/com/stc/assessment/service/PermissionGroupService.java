package com.stc.assessment.service;

import com.stc.assessment.dao.PermissionGroupsRepository;
import com.stc.assessment.enums.PermissionLevelEnum;
import com.stc.assessment.model.PermissionGroups;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class PermissionGroupService {

    protected static final String ADMIN_GROUP = "admin";
    private final PermissionGroupsRepository permissionGroupsRepo;
    private final PermissionService permissionService;

    public PermissionGroups createPermissionGroups() {
        PermissionGroups admin = new PermissionGroups();
        admin.setGroupName(ADMIN_GROUP);
        admin = permissionGroupsRepo.save(admin);

        permissionService.createPermission(admin, PermissionLevelEnum.EDIT, "edit_admin@stc");
        permissionService.createPermission(admin, PermissionLevelEnum.VIEW, "view_admin@stc");

        return admin;
    }

    public PermissionGroups findByGroupName(String name) {
        return permissionGroupsRepo.findByGroupName(name)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "permission group not found!"));
    }

    public void deleteAllPermissionGroups() {
        permissionGroupsRepo.deleteAll();
    }
}

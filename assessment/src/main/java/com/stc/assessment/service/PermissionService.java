package com.stc.assessment.service;

import com.stc.assessment.dao.PermissionRepository;
import com.stc.assessment.enums.PermissionLevelEnum;
import com.stc.assessment.model.PermissionGroups;
import com.stc.assessment.model.Permissions;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class PermissionService {

    private final PermissionRepository permissionRepo;

    public void createPermission(PermissionGroups pg, PermissionLevelEnum permissionLevel, String email) {
        Permissions permission = new Permissions();
        permission.setUserEmail(email);
        permission.setPermissionGroup(pg);
        permission.setPermissionLevel(permissionLevel);

        permissionRepo.save(permission);
    }

    public Permissions findByUserEmailAndPermissionLevel(String email, PermissionLevelEnum permissionLevel) {
        return permissionRepo.findByUserEmailAndPermissionLevel(email, permissionLevel)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "permission not found!"));
    }
    public void deleteAllPermissions() {
        permissionRepo.deleteAll();
    }
}

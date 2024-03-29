package com.stc.assessment.service;

import com.stc.assessment.dao.FilesRepository;
import com.stc.assessment.dao.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final ItemRepository itemRepo;
    private final FilesRepository filesRepo;
    private final PermissionGroupService permissionGroupService;
    private final PermissionService permissionService;

    public void createInitData() {
        permissionGroupService.createPermissionGroups();
    }

    public void deleteAllData() {
        filesRepo.deleteAll();
        itemRepo.deleteAll();
        permissionService.deleteAllPermissions();
        permissionGroupService.deleteAllPermissionGroups();
    }
}

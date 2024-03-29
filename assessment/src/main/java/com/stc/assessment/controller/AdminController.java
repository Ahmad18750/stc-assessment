package com.stc.assessment.controller;

import com.stc.assessment.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("admin")
public class AdminController {
    private final AdminService adminService;

    @PostMapping(value = "/create")
    public void createPermissionGroups() {
        adminService.createInitData();
    }

    @DeleteMapping(value = "/all")
    public void deleteAllData() {
        adminService.deleteAllData();
    }
}

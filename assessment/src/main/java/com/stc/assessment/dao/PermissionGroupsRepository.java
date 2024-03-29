package com.stc.assessment.dao;

import com.stc.assessment.model.PermissionGroups;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PermissionGroupsRepository extends JpaRepository<PermissionGroups, Integer> {

    Optional<PermissionGroups> findByGroupName(String name);
}

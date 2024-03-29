package com.stc.assessment.dao;

import com.stc.assessment.enums.PermissionLevelEnum;
import com.stc.assessment.model.Permissions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permissions, Integer> {

    Optional<Permissions> findByPermissionLevel(PermissionLevelEnum permissionLevel);

    @Query("select p from Permissions p left join fetch p.permissionGroup pg where p.userEmail = :userEmail and p.permissionLevel = :permissionLevel")
    Optional<Permissions> findByUserEmailAndPermissionLevel(@Param("userEmail") String userEmail,
                                                            @Param("permissionLevel") PermissionLevelEnum permissionLevel);
}

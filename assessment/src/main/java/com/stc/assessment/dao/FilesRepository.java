package com.stc.assessment.dao;

import com.stc.assessment.model.Files;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FilesRepository extends JpaRepository<Files, Integer> {

    @Query(value = "select f from Files f left join f.item i left join i.PermissionGroup pg inner join pg.permissions p " +
            "where i.name = :itemName and p.userEmail = :userEmail")
    Optional<Files> getFilesByItemName(@Param("itemName")String itemName, @Param("userEmail") String userEmail);

    boolean existsByItem_Name(String name);
}

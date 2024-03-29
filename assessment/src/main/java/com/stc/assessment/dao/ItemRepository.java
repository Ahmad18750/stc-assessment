package com.stc.assessment.dao;

import com.stc.assessment.dto.ItemDTO;
import com.stc.assessment.enums.ItemTypeEnum;
import com.stc.assessment.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface ItemRepository extends JpaRepository<Item , Integer> {

    boolean existsByName(String name);
    boolean existsByNameAndTypeAndParent_Name(String name, ItemTypeEnum itemType, String parentName);

    @Query("select i from Item i left join fetch i.PermissionGroup pg where i.name = :name")
    Optional<Item> findByName(@Param("name") String name);


    // using projection
    @Query(value = "select i.id, i.name, i.type from item i  " +
            "left join permission_groups pg on pg.id = i.permission_group_id " +
            "left join permissions p on p.group_id = pg.id " +
            "where i.name = :itemName and p.user_email = :userEmail", nativeQuery = true)
    Optional<ItemDTO> getItemMetadataByItemNameAndUserEmail(@Param("itemName")String itemName, @Param("userEmail") String userEmail);
}

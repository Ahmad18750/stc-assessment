package com.stc.assessment.model;

import com.stc.assessment.enums.ItemTypeEnum;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Item {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Integer id;
    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private ItemTypeEnum type;
    @Column(name = "name")
    private String name;

    @ManyToOne(optional = false, fetch = FetchType.LAZY, targetEntity = PermissionGroups.class)
    private PermissionGroups PermissionGroup;

    @ManyToOne(fetch = FetchType.EAGER, targetEntity = Item.class)
    @JoinColumn(name = "parent_id")
    private Item parent;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Item item = (Item) o;

        if (id != null ? !id.equals(item.id) : item.id != null) return false;
        if (type != null ? !type.equals(item.type) : item.type != null) return false;
        if (name != null ? !name.equals(item.name) : item.name != null) return false;
        if (PermissionGroup != null ? !PermissionGroup.equals(item.PermissionGroup) : item.PermissionGroup != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (PermissionGroup != null ? PermissionGroup.hashCode() : 0);
        return result;
    }
}

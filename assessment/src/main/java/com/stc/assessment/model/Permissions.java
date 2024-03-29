package com.stc.assessment.model;

import com.stc.assessment.enums.PermissionLevelEnum;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Permissions {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Integer id;

    @Basic
    @Column(name = "user_email", nullable = false, length = -1)
    private String userEmail;

    @Column(name = "permission_level", nullable = false)
    @Enumerated(EnumType.STRING)
    private PermissionLevelEnum permissionLevel;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name="group_id", nullable=false)
    private PermissionGroups permissionGroup;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Permissions that = (Permissions) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (userEmail != null ? !userEmail.equals(that.userEmail) : that.userEmail != null) return false;
        if (permissionLevel != null ? !permissionLevel.equals(that.permissionLevel) : that.permissionLevel != null)
            return false;
        if (permissionGroup != null ? !permissionGroup.equals(that.permissionGroup) : that.permissionGroup != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (userEmail != null ? userEmail.hashCode() : 0);
        result = 31 * result + (permissionLevel != null ? permissionLevel.hashCode() : 0);
        result = 31 * result + (permissionGroup != null ? permissionGroup.hashCode() : 0);
        return result;
    }
}

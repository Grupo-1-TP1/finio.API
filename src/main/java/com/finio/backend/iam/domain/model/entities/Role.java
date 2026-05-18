package com.finio.backend.iam.domain.model.entities;

import com.finio.backend.iam.domain.exceptions.InvalidRoleException;
import com.finio.backend.iam.domain.model.valueobjects.Roles;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

import java.util.List;

/**
 * Role entity
 * <p>
 * This entity represents the role of a user in the system.
 * It is used to define the permissions of a user.
 * </p>
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@With
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Roles name;

    public Role(Roles name) {
        this.name = name;
    }

    public String getStringName() {
        return name.name();
    }

    public static Role getDefaultRole() {
        return new Role(Roles.ROLE_USER);
    }

    public static Role toRoleFromName(String name) {

        if (name == null) {
            throw new InvalidRoleException("Role name cannot be null");
        }
        try {
            return new Role(Roles.valueOf(name));
        } catch (IllegalArgumentException e) {
            throw new InvalidRoleException("Invalid role name: " + name);
        }
    }

    public static List<Role> validateRoleSet(List<Role> roles) {
        if (roles == null || roles.isEmpty()) {
            return List.of(getDefaultRole());
        }
        return roles;
    }

}

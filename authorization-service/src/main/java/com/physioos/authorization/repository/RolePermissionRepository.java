package com.physioos.authorization.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import com.physioos.authorization.entity.RolePermission;
import com.physioos.authorization.enums.PermissionName;
import com.physioos.authorization.enums.RoleName;

public interface RolePermissionRepository extends JpaRepository<RolePermission,UUID> {
Optional<RolePermission> findByRole_RoleNameAndPermission_PermissionName(RoleName roleName,PermissionName permissionName);
boolean existsByRole_RoleNameAndPermission_PermissionName(RoleName roleName,PermissionName permissionName);
}

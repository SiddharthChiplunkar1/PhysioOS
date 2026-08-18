package com.physioos.authorization.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import com.physioos.authorization.entity.Permission;
import com.physioos.authorization.enums.PermissionName;
public interface PermissionRepository extends JpaRepository<Permission,UUID> {
	List<Permission> findByPermissionName(PermissionName permissionName);
}

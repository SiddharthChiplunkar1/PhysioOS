package com.physioos.authorization.service;

import org.springframework.stereotype.Service;

import com.physioos.authorization.dto.AuthorizationRequest;
import com.physioos.authorization.enums.PermissionName;
import com.physioos.authorization.enums.RoleName;
import com.physioos.authorization.repository.RolePermissionRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class AuthorizationService {
	private final RolePermissionRepository rolePermissionRepository;
	public AuthorizationService(RolePermissionRepository rolePermissionRepository) {
		this.rolePermissionRepository =rolePermissionRepository;
	}
	public boolean hasPermission(AuthorizationRequest dto) {
		return rolePermissionRepository.existsByRole_RoleNameAndPermission_PermissionName(dto.roleName(),dto.permissionName());
	}
}

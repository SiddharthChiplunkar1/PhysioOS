package com.physioos.authorization.dto;

import com.physioos.authorization.enums.PermissionName;
import com.physioos.authorization.enums.RoleName;

public record AuthorizationRequest(RoleName roleName,
PermissionName permissionName) {
	
}

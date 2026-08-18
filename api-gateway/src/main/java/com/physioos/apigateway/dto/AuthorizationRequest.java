package com.physioos.apigateway.dto;


public record AuthorizationRequest(String roleName,
String permissionName) {
	
}

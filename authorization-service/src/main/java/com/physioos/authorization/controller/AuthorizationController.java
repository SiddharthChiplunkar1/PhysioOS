package com.physioos.authorization.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.physioos.authorization.dto.AuthorizationRequest;
import com.physioos.authorization.dto.AuthorizationResponse;
import com.physioos.authorization.service.AuthorizationService;

@RestController
@RequestMapping("/api/authorization")
public class AuthorizationController {
	private final AuthorizationService authorizationService;
	public AuthorizationController(AuthorizationService authorizationService) {this.authorizationService=authorizationService;}
	@PostMapping("/check")
	public AuthorizationResponse checkAuthorization(@RequestBody AuthorizationRequest request) {
		boolean allowed=authorizationService.hasPermission(request);
		return new AuthorizationResponse(allowed);
	}
}

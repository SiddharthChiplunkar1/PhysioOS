package com.physioos.apigateway.config;
import java.util.List;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class JwtAuthenticationConverter implements Converter<Jwt,AbstractAuthenticationToken> {
	@Override
	public AbstractAuthenticationToken convert(Jwt jwt) {
		String role=jwt.getClaimAsString("role");
		List<SimpleGrantedAuthority> authorities=List.of(new SimpleGrantedAuthority(role));
		return new JwtAuthenticationToken(jwt,authorities);
	}
}

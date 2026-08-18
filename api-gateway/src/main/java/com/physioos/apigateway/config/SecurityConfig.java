package com.physioos.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.config.Customizer;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;

@Configuration
@EnableWebFluxSecurity

public class SecurityConfig {
	@Bean
	public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
		return http.csrf(ServerHttpSecurity.CsrfSpec::disable).authorizeExchange(exchanges->exchanges.pathMatchers("/api/auth/**").permitAll().anyExchange().authenticated())
				.oauth2ResourceServer(oauth2 -> oauth2
				        .jwt(jwt->jwt.jwtAuthenticationConverter( new ReactiveJwtAuthenticationConverterAdapter(new JwtAuthenticationConverter())))
				).build();
	}
}

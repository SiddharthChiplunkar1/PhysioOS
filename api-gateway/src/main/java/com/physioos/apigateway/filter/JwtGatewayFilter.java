package com.physioos.apigateway.filter;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;
@Component
public class JwtGatewayFilter implements GlobalFilter,Ordered{
	@Override
	public Mono<Void> filter(ServerWebExchange exchange,GatewayFilterChain chain){
		return ReactiveSecurityContextHolder.getContext().flatMap(context->{
			if(context.getAuthentication() instanceof JwtAuthenticationToken jwtAuth) {
				var jwt=jwtAuth.getToken();
				String userId=jwt.getClaimAsString("userId");
				String role=jwt.getClaimAsString("role");
				String organizationId=jwt.getClaimAsString("organizationId");
				 ServerWebExchange mutatedExchange=exchange.mutate().request(request->request.header("X-User-Id",userId).header("X-User-Role",role).header(
                                             "X-Organization-Id",organizationId)).build();
                 return chain.filter(mutatedExchange);

			}
			return chain.filter(exchange);
			
		});
	}
	@Override
	public int getOrder() {return -1;}
}

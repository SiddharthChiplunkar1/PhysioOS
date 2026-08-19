package com.physioos.apigateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import com.physioos.apigateway.client.AuthorizationClient;
import com.physioos.apigateway.dto.AuthorizationRequest;
import com.physioos.apigateway.dto.AuthorizationResponse;
import reactor.core.publisher.Mono;
@Component
public class AuthorizationFilter implements GlobalFilter, Ordered {
    private final AuthorizationClient authorizationClient;
    public AuthorizationFilter(AuthorizationClient authorizationClient) {
        this.authorizationClient=authorizationClient;
    }
    @Override
    public Mono<Void> filter(ServerWebExchange exchange,GatewayFilterChain chain) {
        return exchange.getAttribute(org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR)==null? chain.filter(exchange)
                : checkAuthorization(exchange, chain);
    }
    private Mono<Void> checkAuthorization(ServerWebExchange exchange,GatewayFilterChain chain) {
        Route route = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
        if(route==null) {return chain.filter(exchange);}

        Object permissionMetadata=route.getMetadata().get("permission");
        if (permissionMetadata==null){return chain.filter(exchange);}
        String permissionName=permissionMetadata.toString();
        return ReactiveSecurityContextHolder.getContext().flatMap(context->{
        	if (!(context.getAuthentication()instanceof JwtAuthenticationToken jwtAuth)) {
                        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                        return exchange.getResponse().setComplete();
                    }

                    String roleName=jwtAuth.getToken().getClaimAsString("role");
                    if(roleName==null||roleName.isBlank())exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                    AuthorizationRequest request=new AuthorizationRequest(roleName,permissionName);
                    return authorizationClient.checkPermission(request).flatMap(response -> {
                    				if (response.allowed()) {
                                    return chain.filter(exchange);
                                }
                                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                                return exchange.getResponse().setComplete();
                    });
        });
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
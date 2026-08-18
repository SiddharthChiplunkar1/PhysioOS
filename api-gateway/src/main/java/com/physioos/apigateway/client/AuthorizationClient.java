package com.physioos.apigateway.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.physioos.apigateway.dto.AuthorizationRequest;
import com.physioos.apigateway.dto.AuthorizationResponse;

import reactor.core.publisher.Mono;

@Component
public class AuthorizationClient {
    private final WebClient webClient;
    public AuthorizationClient(
            WebClient.Builder webClientBuilder,@Value("${authorization.service.url}") String authorizationServiceUrl) {
        this.webClient =webClientBuilder.baseUrl(authorizationServiceUrl).build();
    }
    public Mono<AuthorizationResponse> checkPermission(AuthorizationRequest request) {
        return webClient.post().uri("/api/authorization/check").bodyValue(request).retrieve().bodyToMono(AuthorizationResponse.class);
    }
}
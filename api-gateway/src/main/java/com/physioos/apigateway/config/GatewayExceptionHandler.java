package com.physioos.apigateway.config;


import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import reactor.core.publisher.Mono;

@Component
@Order(-2)
public class GatewayExceptionHandler implements ErrorWebExceptionHandler{
	private final ObjectMapper objectMapper;
	public GatewayExceptionHandler(ObjectMapper objectMapper) {
		this.objectMapper=objectMapper;
	}
	@Override
	public Mono<Void> handle(ServerWebExchange exchange, Throwable ex){
		if(exchange.getResponse().isCommitted())return Mono.error(ex);
		exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
		exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
		Map<String,Object> body=Map.of("status",500,"error","Internal Gateway Error","message","An unexpected error occurred");
		try {
			byte[] bytes=objectMapper.writeValueAsBytes(body);
			return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(bytes)));
		}catch(Exception e) {
			return Mono.error(e);
		}
	}
}

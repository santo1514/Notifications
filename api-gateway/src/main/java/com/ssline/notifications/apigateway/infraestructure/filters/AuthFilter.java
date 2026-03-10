package com.ssline.notifications.apigateway.infraestructure.filters;


import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import com.ssline.notifications.apigateway.usecases.ValidateRequestUseCase;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import reactor.core.publisher.Mono;

@Component
public class AuthFilter extends AbstractGatewayFilterFactory<AuthFilter.Config> {

    private final ValidateRequestUseCase validateRequestUseCase;

    public AuthFilter(ValidateRequestUseCase validateRequestUseCase) {
        super(Config.class);
        this.validateRequestUseCase = validateRequestUseCase;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            var request = exchange.getRequest();
            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Falta token de acceso"));
            }
            var authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            return validateRequestUseCase.execute(authHeader)
                .flatMap(session -> {
                    var modifiedRequest = exchange.getRequest().mutate()
                        .header("X-User-Id", session.userId().toString())
                        .header("X-User-Email", session.email())
                        .build();
                    return chain.filter(exchange.mutate().request(modifiedRequest).build());
                })
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token inválido o expirado")));
        };
    }
    public static class Config {
    }
}
package com.ssline.notifications.apigateway.infraestructure.config;

import com.ssline.notifications.apigateway.infraestructure.filters.AuthFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    private final AuthFilter authFilter;

    public GatewayConfig(AuthFilter authFilter) {
        this.authFilter = authFilter;
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
            // Ruta para el Auth Service (Sin filtro de seguridad para login/registro)
            .route("auth-service", r -> r.path("/auth/**")
                .uri("lb://auth-service"))
            
            // Ruta para el Notification Service (Protegida por nuestro AuthFilter)
            .route("notification-service", r -> r.path("/notifications/**")
                .filters(f -> f.filter(authFilter.apply(new AuthFilter.Config())))
                .uri("lb://notification-service"))
            .build();
    }
}

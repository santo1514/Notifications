package com.ssline.notifications.apigateway.infraestructure.documentation;

import org.springdoc.core.properties.AbstractSwaggerUiConfigProperties;
import org.springdoc.core.properties.SwaggerUiConfigParameters;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import jakarta.annotation.PostConstruct;
import java.util.stream.Collectors;


/**
 * Agregador dinámico para Swagger UI.
 * Escanea las rutas definidas en el Gateway y registra los endpoints de OpenAPI
 * de cada microservicio para centralizarlos en localhost:8080/swagger-ui.html
 */
@Configuration
@Primary
public class SwaggerAggregator {

    private final RouteDefinitionLocator locator;
    private final SwaggerUiConfigParameters swaggerUiConfigParameters;

    public SwaggerAggregator(RouteDefinitionLocator locator, SwaggerUiConfigParameters swaggerUiConfigParameters) {
        this.locator = locator;
        this.swaggerUiConfigParameters = swaggerUiConfigParameters;
    }

    @PostConstruct
    public void init() {
        locator.getRouteDefinitions()
            .filter(routeDefinition -> routeDefinition.getId().matches(".*-service")) // Filtramos por ID (ej. auth-service)
            .map(routeDefinition -> {
                String name = routeDefinition.getId();
                // Construimos la URL donde el Gateway reenviará la petición del JSON de Swagger
                // Por convención, cada microservicio expone su doc en /v3/api-docs
                String url = "/" + name + "/v3/api-docs";

                var swaggerUrl = new AbstractSwaggerUiConfigProperties.SwaggerUrl();
                swaggerUrl.setName(name);
                swaggerUrl.setUrl(url);
                swaggerUrl.setDisplayName(name.toUpperCase().replace("-", " "));
                return swaggerUrl;
            })
            .collect(Collectors.toSet())
            .subscribe(swaggerUiConfigParameters::setUrls);
    }
}

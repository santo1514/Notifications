package com.ssline.notifications.apigateway.infraestructure.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class SwaggerConfig {
        
    @Bean
    public List<GroupedOpenApi> apis(RouteDefinitionLocator locator) {
        var groups = new ArrayList<GroupedOpenApi>();
        
        groups.add(GroupedOpenApi.builder()
                .group("auth-service")
                .pathsToMatch("/auth/**")
                .build());
                
        groups.add(GroupedOpenApi.builder()
                .group("notification-service")
                .pathsToMatch("/notifications/**")
                .build());
                
        return groups;
    }
}
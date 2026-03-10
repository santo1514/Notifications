package com.ssline.notifications.apigateway.infraestructure.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;

@Component
@Order(-2) // Prioridad alta para capturar errores antes que el handler por defecto
@SuppressWarnings("null")
public class GlobalErrorFilter implements ErrorWebExceptionHandler {

    private final ObjectMapper objectMapper;

    public GlobalErrorFilter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        var response = exchange.getResponse();
        
        if (response.isCommitted()) {
            return Mono.error(ex);
        }

        // Determinar el status code
        var status = HttpStatus.INTERNAL_SERVER_ERROR;
        if (ex instanceof ResponseStatusException rsEx) {
            status = HttpStatus.valueOf(rsEx.getStatusCode().value());
        }

        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        // Estructura de error usando un Map o un Record
        var errorBody = Map.of(
            "timestamp", LocalDateTime.now().toString(),
            "path", exchange.getRequest().getPath().value(),
            "status", status.value(),
            "error", status.getReasonPhrase(),
            "message", ex.getMessage()
        );

        return response.writeWith(Mono.fromSupplier(() -> {
            var bufferFactory = response.bufferFactory();
            try {
                return bufferFactory.wrap(objectMapper.writeValueAsBytes(errorBody));
            } catch (Exception e) {
                return bufferFactory.wrap(new byte[0]);
            }
        }));
    }
}
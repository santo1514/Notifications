package com.ssline.notifications.apigateway.core.ports.inputs;

import com.ssline.notifications.apigateway.core.domain.UserSession;

import reactor.core.publisher.Mono;

/**
 * Puerto de entrada/salida para la validación de tokens de acceso.
 * Define el contrato que la infraestructura debe cumplir para transformar
 * un token en una sesión de usuario válida.
 */
public interface TokenValidator {
    
    /**
     * Valida la autenticidad y vigencia de un token.
     * @param token El String del token extraído del header Authorization.
     * @return Un Mono que emite el UserSession si es válido, o se completa vacío si no.
     */
    Mono<UserSession> validate (String token);
    /**
     * Verifica si el formato del token es soportado (ej. si empieza con 'Bearer ').
     * @param authorizationHeader El valor completo del header de autorización.
     * @return true si el formato es correcto.
     */
    default boolean isSupported(String authorizationHeader){
        return authorizationHeader != null && authorizationHeader.startsWith("Bearer ");
    }
    /**
     * Limpia el prefijo del token para obtener el valor puro.
     * @param authorizationHeader El valor completo del header.
     * @return El token sin el prefijo 'Bearer '.
     */
    default String extractRawToken(String authorizationHeader){
        return authorizationHeader.substring(7); // Elimina "Bearer " del inicio
    }
}

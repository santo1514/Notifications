package com.ssline.notifications.apigateway.core.domain;

import java.util.UUID;

/**
 * Representa la sesión del usuario autenticado dentro del ecosistema.
 * Se utiliza para propagar la identidad a través de los filtros del Gateway.
 * Por qué esta definición de record:
 *  Inmutabilidad: Al ser un record, todos los campos son final. Esto garantiza que una vez que el TokenValidator crea la sesión, nadie pueda manipular el ID del usuario accidentalmente.
 *  Agnóstico: No importa si el token es JWT, Paseto o una sesión de Redis. El Gateway convierte la "evidencia de identidad" en este objeto de dominio.
 *  Seguridad en el Filtro: Cuando el AuthFilter valide la petición, adjuntará este UserSession al contexto de la petición (o a los headers que se reenvían al microservicio de destino).
 */
public record UserSession (
    UUID userId,
    String email,
    Long expirationTime
){
    // Constructor compacto para validaciones de dominio
    public UserSession{
        if(userId == null) 
            throw new IllegalArgumentException("El userId no puede ser nulo");
        if (email == null || email.isBlank()) 
            throw new IllegalArgumentException("El email no puede ser nulo o estar vacío");

    }
}

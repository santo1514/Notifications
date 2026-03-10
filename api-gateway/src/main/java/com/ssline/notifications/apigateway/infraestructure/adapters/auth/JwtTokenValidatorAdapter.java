package com.ssline.notifications.apigateway.infraestructure.adapters.auth;

import com.ssline.notifications.apigateway.core.ports.inputs.TokenValidator;
import com.ssline.notifications.apigateway.core.domain.UserSession;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.spec.SecretKeySpec;
import reactor.core.publisher.Mono;
import java.nio.charset.StandardCharsets;
import java.util.UUID;


@Component
public class JwtTokenValidatorAdapter implements TokenValidator {

    private static final Logger log = LoggerFactory.getLogger(JwtTokenValidatorAdapter.class);
    private final ReactiveJwtDecoder jwtDecoder;
    @Value("${app.global.validation.email}")
    private final String emailPattern;

    public JwtTokenValidatorAdapter(
            @Value("${app.global.security.jwt.secret}") String secret,
            @Value("${app.global.validation.email}") String emailPattern) {
        var secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        this.jwtDecoder = NimbusReactiveJwtDecoder.withSecretKey(secretKey).build();
        this.emailPattern = emailPattern;
    }

    @SuppressWarnings("null")
    @Override
    public Mono<UserSession> validate(String token) {
        return jwtDecoder.decode(token)
                .filter(jwt -> jwt.getExpiresAt() != null && jwt.getClaimAsString("email") != null)
                .filter(jwt -> jwt.getClaimAsString("email").matches(emailPattern))
                .map(jwt -> new UserSession(
                        UUID.fromString(jwt.getSubject()),
                        jwt.getClaimAsString("email"),
                        jwt.getExpiresAt().toEpochMilli()
                ))
                .onErrorResume(ex -> {
                    log.error("Error validating JWT token", ex);
                    return Mono.empty();
                });
    }
}
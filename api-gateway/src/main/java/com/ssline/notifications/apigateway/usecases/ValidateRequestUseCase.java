package com.ssline.notifications.apigateway.usecases;

import com.ssline.notifications.apigateway.core.domain.UserSession;
import com.ssline.notifications.apigateway.core.ports.inputs.TokenValidator;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ValidateRequestUseCase {

    private final TokenValidator tokenValidator;

    public ValidateRequestUseCase(TokenValidator tokenValidator) {
        this.tokenValidator = tokenValidator;
    }

    public Mono<UserSession> execute(String authHeader) {
        if (!tokenValidator.isSupported(authHeader)) {
            return Mono.empty();
        }
        String rawToken = tokenValidator.extractRawToken(authHeader);
        return tokenValidator.validate(rawToken);
    }
}
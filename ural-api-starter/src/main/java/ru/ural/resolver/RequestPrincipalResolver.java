package ru.ural.resolver;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.server.ResponseStatusException;
import ru.ural.annotations.RequestPrincipals;

import java.util.Optional;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class RequestPrincipalResolver implements HandlerMethodArgumentResolver {

    private final AuthPrincipalParser parser;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(RequestPrincipals.class);
    }

    @Nullable
    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            @Nullable ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            @Nullable WebDataBinderFactory binderFactory
    ) {
        Optional<Object> userInfoOpt = parser.parsePrincipal(webRequest);
        RequestPrincipals requestPrincipals = parameter.getParameterAnnotation(RequestPrincipals.class);
        if (requestPrincipals.required() && userInfoOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        if (userInfoOpt.isEmpty()) {
            return null;
        }

        Set<ConstraintViolation<Object>> violations;

        try (ValidatorFactory vFactory = Validation.buildDefaultValidatorFactory()) {
            violations = vFactory.getValidator().validate(userInfoOpt.get());
        } catch (RuntimeException exception) {
            log.error("Can't validate parameter PRINCIPAL [ {} ]", parameter, exception);
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE);
        }

        if (!violations.isEmpty()) {
            log.error("Parameter PRINCIPAL [ {} ] validation failed: {}", parameter, violations);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        return userInfoOpt.get();
    }

}

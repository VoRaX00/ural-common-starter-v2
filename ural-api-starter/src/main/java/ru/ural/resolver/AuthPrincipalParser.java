package ru.ural.resolver;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.WebRequest;
import ru.ural.dto.UserPrincipals;

import java.io.IOException;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AuthPrincipalParser implements PrincipalParser {

    private static final String AUTHORIZATION_HEADER_NAME = "Authorization";

    private final ObjectMapper objectMapper;

    @Override
    public Optional<Object> parsePrincipal(WebRequest webRequest) {
        String token = webRequest.getHeader(AUTHORIZATION_HEADER_NAME);

        if (!StringUtils.hasText(token)) {
            return Optional.empty();
        }

        String[] jwtParts = token.replace("Bearer ", token).split("\\.");
        if (jwtParts.length < 2) {
            return Optional.empty();
        }

        String body = new String(Base64.getDecoder().decode(jwtParts[1]));
        Map<String, Object> payload;

        try {
            payload = objectMapper.readValue(body, new TypeReference<>() { });
        } catch (IOException e) {
            return Optional.empty();
        }

        String uuid = (String) payload.get("uuid");
        String email = (String) payload.get("email");
        String username = (String) payload.get("username");

        UserPrincipals userInfo = new UserPrincipals(uuid, email, username);
        return Optional.of(userInfo);
    }
}

package com.supergroup.auth.domain.util;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;

/**
 * Utils for jwt
 */
public class JwtUtils {

    public static Optional<String> getJwtToken(HttpServletRequest request) {
        var authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        return StringUtils.hasText(authorization) && authorization.startsWith("Bearer ")
               ? Optional.of(authorization.split(" ")[1].trim())
               : Optional.empty();
    }

    public static Optional<String> getJwtToken(String authorization) {
        return StringUtils.hasText(authorization) && authorization.startsWith("Bearer ")
               ? Optional.of(authorization.split(" ")[1].trim())
               : Optional.empty();
    }

}

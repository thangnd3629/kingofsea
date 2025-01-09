package com.supergroup.kos.middleware;

import java.io.IOException;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.supergroup.auth.domain.provider.token.JwtTokenProvider;
import com.supergroup.auth.domain.repository.persistence.UserCacheRepository;
import com.supergroup.auth.domain.service.AuthService;
import com.supergroup.auth.domain.util.JwtUtils;
import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.config.AccessSession;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtTokenProvider    jwtTokenProvider;
    private final ObjectMapper        objectMapper;
    private final AuthService         authService;
    private final UserCacheRepository userCacheRepository;

    @SuppressWarnings("checkstyle:OperatorWrap")
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {
        // Get authorization header and validate
        try {
            JwtUtils.getJwtToken(request).ifPresent(token -> {

                // Get user identity and set it on the spring security context
                var accessTokenClaim = jwtTokenProvider.getAccessTokenClaim(token);
                var userCache = userCacheRepository.getUserById(accessTokenClaim.getUserId())
                                                   .orElseThrow(() -> KOSException.of(ErrorCode.USER_NOT_FOUND));

                if (!userCache.isEnabled()) {
                    throw KOSException.of(ErrorCode.USER_WAS_DELETED_OR_BANNED);
                }

                // check logout
                if (authService.isLogout(token)) {
                    throw KOSException.of(ErrorCode.USER_IS_LOGGED_OUT);
                }

                var accessSession = new AccessSession(accessTokenClaim.getUserId(),
                                                      userCache.getUsername(),
                                                      userCache.getPassword(),
                                                      userCache.getAuthorities(),
                                                      accessTokenClaim.getSessionId(),
                                                      userCache.isEnabled()
                );
                var authentication = new UsernamePasswordAuthenticationToken(
                        accessSession,
                        null,
                        userCache.getAuthorities());

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            });
        } catch (ExpiredJwtException ex) {
            response.resetBuffer();
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
            response.getOutputStream().print(objectMapper.writeValueAsString(Map.of("status", ErrorCode.TOKEN_EXPIRED)));
            response.flushBuffer(); // marks response as committed -- if we don't do this the request will go through normally!
            return;
        } catch (KOSException ex) {
            response.resetBuffer();
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
            response.getOutputStream().print(objectMapper.writeValueAsString(Map.of("status", ex.getCode())));
            response.flushBuffer(); // marks response as committed -- if we don't do this the request will go through normally!
            return;
        }
        chain.doFilter(request, response);
    }

}
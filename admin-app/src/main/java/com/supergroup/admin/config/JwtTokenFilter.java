package com.supergroup.admin.config;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.supergroup.admin.domain.provider.AdminJwtTokenProvider;
import com.supergroup.admin.domain.repository.AdminAccountRepository;
import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private final AdminJwtTokenProvider  jwtTokenProvider;
    private final AdminAccountRepository adminAccountRepository;
    private final ObjectMapper           objectMapper;

    @SuppressWarnings("checkstyle:OperatorWrap")
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {
        // Get authorization header and validate
        try {
            jwtTokenProvider.getJwt(request).ifPresent(token -> {

                // Get user identity and set it on the spring security context
                UserDetails userDetails = adminAccountRepository.findById(jwtTokenProvider.getAccountId(token)).orElseThrow(
                        () -> KOSException.of(ErrorCode.TOKEN_EXPIRED));

                var authentication = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails == null ? List.of() : userDetails.getAuthorities());

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
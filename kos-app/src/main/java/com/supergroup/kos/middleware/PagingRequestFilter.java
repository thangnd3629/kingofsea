package com.supergroup.kos.middleware;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class PagingRequestFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            var page = Integer.parseInt(request.getParameter("page"));
            var size = Integer.parseInt(request.getParameter("size"));
            request.setAttribute("pageable", PageRequest.of(page, size));
        } catch (Exception e) {
            // ignore
        }
        filterChain.doFilter(request, response);
    }
}

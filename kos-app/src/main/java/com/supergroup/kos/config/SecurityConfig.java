package com.supergroup.kos.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.supergroup.auth.domain.service.UserService;
import com.supergroup.kos.middleware.JwtTokenFilter;

import lombok.RequiredArgsConstructor;

@EnableWebSecurity
@RequiredArgsConstructor
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * Hardcode for internal system routers.
     */
    private static final String[] WHITELIST_API = {
            "/v1/auth/login",
            "/v1/auth/register/**",
            "/v1/auth/password/forgot",
            "/v1/auth/password/reset",
            "/v1/auth/refresh",
            "/v1/auth/email/verify",
            "/v1/verify/resend",
            "/v1/public/**",
            "/v1/user/kos/config/frequency",
            "/",
            };

    private final JwtTokenFilter   jwtTokenFilter;

    private final UserService     userService;
    private final PasswordEncoder passwordEncoder;

    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        // Get AuthenticationManager bean
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth)
            throws Exception {
        auth.userDetailsService(userService)
            .passwordEncoder(passwordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http = http.httpBasic()
                   .and()
                   .sessionManagement()
                   .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                   .and()
                   .authorizeRequests()
                   .antMatchers(WHITELIST_API).permitAll()
                   .and()
                   .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
//                   .addFilterAfter(miningMiddleware, JwtTokenFilter.class)
//                   .addFilterAfter(itemMiddleware, MiningMiddleware.class)
                   .csrf().disable();

        http.authorizeRequests()
            .anyRequest()
            .authenticated()
            .and().cors();
    }

    @Bean
    public CorsFilter corsFilter() {
        var source = new UrlBasedCorsConfigurationSource();
        var config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOriginPatterns(List.of("*"));
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
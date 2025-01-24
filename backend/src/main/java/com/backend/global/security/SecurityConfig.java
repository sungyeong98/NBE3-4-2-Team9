package com.backend.global.security;

import com.backend.global.response.GenericResponse;
import com.backend.standard.util.Ut;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, CustomAuthenticationFilter customAuthenticationFilter) throws Exception {
        http.
                authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/h2-console/**").permitAll()  // TODO (현재는 h2-console에 대해서만 적용함)

                )
                .headers(headers ->
                        headers.frameOptions(frameOptions ->
                                frameOptions.sameOrigin()))
                .csrf(csrf -> csrf.disable())
                .addFilterBefore(customAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(
                        exceptionHandling -> exceptionHandling
                                .authenticationEntryPoint(
                                        (request, response, authException) -> {
                                            response.setContentType("application/json;charset=utf-8");
                                            response.setStatus(401);
                                            response.getWriter().write(
                                                    Ut.json.toString(
                                                            GenericResponse.of(false, "401")
                                                    )
                                            );
                                        }
                                )
                                .accessDeniedHandler(
                                        (request, response, authException) -> {
                                            response.setContentType("application/json;charset=utf-8");
                                            response.setStatus(403);
                                            response.getWriter().write(
                                                    Ut.json.toString(
                                                            GenericResponse.of(false, "403")
                                                    )
                                            );
                                        }
                                )
                );

        return http.build();
    }

}

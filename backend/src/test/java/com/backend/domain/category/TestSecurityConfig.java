package com.backend.global.category;

import com.backend.standard.util.JwtUtil;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.web.SecurityFilterChain;

@TestConfiguration
public  class TestSecurityConfig {
    @Bean
    public JwtUtil jwtUtil() {
        return new JwtUtil("testsecretkeytestsecretkeytestsecretkeytestsecretkey");
    }

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        ClientRegistration dummyRegistration = ClientRegistration.withRegistrationId("dummy")
                .clientId("dummy-client-id")
                .clientSecret("dummy-client-secret")
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
                .scope("read")
                .authorizationUri("https://example.com/oauth2/authorize")
                .tokenUri("https://example.com/oauth2/token")
                .userInfoUri("https://example.com/userinfo")
                .userNameAttributeName("id")
                .clientName("dummy")
                .build();
        return new InMemoryClientRegistrationRepository(dummyRegistration);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }
}

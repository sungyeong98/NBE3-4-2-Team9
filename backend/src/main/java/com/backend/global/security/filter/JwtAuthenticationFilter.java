package com.backend.global.security.filter;

import com.backend.domain.user.dto.request.LoginRequest;
import com.backend.domain.user.dto.response.LoginResponse;
import com.backend.global.redis.repository.RedisRepository;
import com.backend.global.response.GenericResponse;
import com.backend.global.security.custom.CustomUserDetails;
import com.backend.standard.util.AuthResponseUtil;
import com.backend.standard.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final JwtUtil jwtUtil;
    private final long ACCESS_EXPIRATION;
    private final long REFRESH_EXPIRATION;
    private final ObjectMapper objectMapper;
    private final RedisRepository redisRepository;
    private final AuthenticationManager authenticationManager;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse resp) throws AuthenticationException {
        try {
            LoginRequest loginRequest = objectMapper.readValue(req.getInputStream(), LoginRequest.class);

            String email = loginRequest.getEmail();
            String password = loginRequest.getPassword();

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email, password);

            return authenticationManager.authenticate(authToken);
        } catch (IOException e) {
            throw new AuthenticationServiceException("잘못된 로그인 정보입니다.");
        }
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest req, HttpServletResponse resp, AuthenticationException failed) throws IOException {
        AuthResponseUtil.failLogin(
                resp,
                GenericResponse.of(false, "400"),
                HttpServletResponse.SC_UNAUTHORIZED,
                objectMapper
        );
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse resp, FilterChain chain, Authentication authentication) throws IOException {

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        String accessToken = jwtUtil.createAccessToken(userDetails, ACCESS_EXPIRATION);
        String refreshToken = jwtUtil.createRefreshToken(userDetails, REFRESH_EXPIRATION);

        redisRepository.remove(userDetails.getUsername());
        redisRepository.save(userDetails.getUsername(), refreshToken, REFRESH_EXPIRATION, TimeUnit.MILLISECONDS);

        AuthResponseUtil.success(
                resp,
                accessToken,
                jwtUtil.setJwtCookie("refreshToken", refreshToken, REFRESH_EXPIRATION),
                HttpServletResponse.SC_OK,
                GenericResponse.of(true, "200", new LoginResponse(userDetails.getUsername()), "로그인이 성공적으로 이루어졌습니다."),
                objectMapper
        );
    }

}

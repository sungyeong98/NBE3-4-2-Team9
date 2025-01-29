package com.backend.global.security.handler;

import com.backend.global.redis.repository.RedisRepository;
import com.backend.standard.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class JwtLogoutHandler implements LogoutHandler {

    private final JwtUtil jwtUtil;
    private final RedisRepository redisRepository;

    @Override
    public void logout(HttpServletRequest req, HttpServletResponse resp, Authentication auth) {
        String authorization = req.getHeader("Authorization");
        String accessToken = null;

        String refreshToken = getRefreshToken(req);

        if (authorization != null && refreshToken != null) {
            accessToken = authorization.substring(7);

            try {
                String username = jwtUtil.getUsername(accessToken);
                Date expiration = jwtUtil.getExpirationDate(accessToken);
                long duration = expiration.getTime() - System.currentTimeMillis();
                redisRepository.save(accessToken, "Logout", duration, TimeUnit.MILLISECONDS);
                if (redisRepository.get(username).equals(refreshToken)) redisRepository.remove(username);
            } catch (Exception e) {
            }
        }
    }

    private String getRefreshToken(HttpServletRequest req) {
        if (req.getCookies() == null) return null;

        String refreshToken = null;

        for (Cookie cookie : req.getCookies()) {
            if (cookie.getName().equals("refreshToken")) refreshToken = cookie.getValue();
        }

        return refreshToken;
    }

}

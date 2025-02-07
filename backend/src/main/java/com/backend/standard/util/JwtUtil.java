package com.backend.standard.util;

import com.backend.domain.user.entity.SiteUser;
import com.backend.global.exception.GlobalErrorCode;
import com.backend.global.exception.GlobalException;
import com.backend.global.security.custom.CustomUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.Cookie;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

    private final SecretKey secretKey;

    public JwtUtil(@Value("${jwt.secret}") String secret) {
        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS512.key().build().getAlgorithm());
    }

    public Long getUserId(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("id", Long.class);
    }

    public String getUsername(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("username", String.class);
    }

    public String getRole(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("role", String.class);
    }

    public boolean isExpired(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
    }

    public Date getExpirationDate(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration();
    }

    public String createAccessToken(CustomUserDetails customUserDetails, long expiration) {
        long currentTime = System.currentTimeMillis();

//        return "Bearer " + Jwts.builder()
        return Jwts.builder()
                .claim("subject", "access")
                .claim("id", customUserDetails.getSiteUser().getId())
                .claim("username", customUserDetails.getUsername())
                .claim("role", customUserDetails.getSiteUser().getUserRole())
                .issuedAt(new Date(currentTime))
                .expiration(new Date(currentTime + expiration))
                .signWith(secretKey)
                .compact();
    }

    public String createRefreshToken(CustomUserDetails customUserDetails, long expiration) {
        long currentTime = System.currentTimeMillis();

        return Jwts.builder()
                .claim("subject", "refresh")
                .claim("id", customUserDetails.getSiteUser().getId())
                .claim("username", customUserDetails.getUsername())
                .claim("role", customUserDetails.getSiteUser().getUserRole())
                .issuedAt(new Date(currentTime))
                .expiration(new Date(currentTime + expiration))
                .signWith(secretKey)
                .compact();
    }


    public Cookie setJwtCookie(String key, String value, long expiration) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge((int) expiration / 1000);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setHttpOnly(true);

        return cookie;
    }

    public Authentication getAuthentication(String token) {
        String username = getUsername(token);
        String role = getRole(token);
        Long userId = getUserId(token);

        CustomUserDetails userDetails = new CustomUserDetails(
                SiteUser.builder()
                        .id(userId)
                        .email(username)
                        .userRole(role)
                        .build()
        );

        return new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
    }

    /**
     * ✅ JWT 토큰에서 Claims(정보) 가져오기
     */
    public Claims getClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException e) {
            throw new GlobalException(GlobalErrorCode.INVALID_TOKEN);
        }
    }

    /**
     * ✅ JWT 토큰 검증 (유효성 체크)
     */
    public void validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
        } catch (ExpiredJwtException e) {
            throw new GlobalException(GlobalErrorCode.EXPIRED_TOKEN);
        } catch (JwtException | IllegalArgumentException e) {
            throw new GlobalException(GlobalErrorCode.INVALID_TOKEN);
        }
    }

}

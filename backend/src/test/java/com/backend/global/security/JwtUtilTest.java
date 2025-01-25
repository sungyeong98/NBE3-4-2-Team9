package com.backend.global.security;

import com.backend.domain.user.entity.SiteUser;
import com.backend.global.security.custom.CustomUserDetails;
import com.backend.standard.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
public class JwtUtilTest {

    @Autowired
    private JwtUtil jwtUtil;

    @Value("${jwt.token.access-expiration}")
    private long ACCESS_EXPIRATION;

    @Value("${jwt.token.refresh-expiration}")
    private long REFRESH_EXPIRATION;

    @Test
    @DisplayName("엑세스 토큰 생성 및 검증")
    void test1() {
        CustomUserDetails userDetails = new CustomUserDetails(
            SiteUser.builder()
                    .email("test@gmail.com")
                    .userRole("ROLE_ADMIN")
                    .build()
        );

        String accessToken = jwtUtil.createAccessToken(userDetails, ACCESS_EXPIRATION).substring(7);
        assertNotNull(accessToken);

        String email = jwtUtil.getUsername(accessToken);
        assertEquals("test@gmail.com", email);

        String role = jwtUtil.getRole(accessToken);
        assertEquals("ROLE_ADMIN", role);
    }

    @Test
    @DisplayName("refresh 토큰 생성 및 검증")
    void test2() {
        CustomUserDetails userDetails = new CustomUserDetails(
                SiteUser.builder()
                        .email("test@gmail.com")
                        .userRole("ROLE_ADMIN")
                        .build()
        );

        String refreshToken = jwtUtil.createRefreshToken(userDetails, REFRESH_EXPIRATION);
        assertNotNull(refreshToken);

        String email = jwtUtil.getUsername(refreshToken);
        assertEquals("test@gmail.com", email);

        String role = jwtUtil.getRole(refreshToken);
        assertEquals("ROLE_ADMIN", role);
    }

    @Test
    @DisplayName("엑세스 토큰 만료시간 검증")
    void test3() {
        CustomUserDetails userDetails = new CustomUserDetails(
                SiteUser.builder()
                        .email("test@gmail.com")
                        .userRole("ROLE_ADMIN")
                        .build()
        );

        String expiredToken = jwtUtil.createAccessToken(userDetails, -1000).substring(7);

        assertThrows(ExpiredJwtException.class, () -> jwtUtil.isExpired(expiredToken));
    }

}

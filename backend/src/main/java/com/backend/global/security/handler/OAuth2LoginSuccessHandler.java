package com.backend.global.security.handler;

import com.backend.domain.user.dto.response.KakaoLoginResponse;
import com.backend.domain.user.entity.SiteUser;
import com.backend.domain.user.entity.UserRole;
import com.backend.global.config.AppConfig;
import com.backend.global.redis.repository.RedisRepository;
import com.backend.global.response.GenericResponse;
import com.backend.global.security.custom.CustomUserDetails;
import com.backend.standard.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final RedisRepository redisRepository;
    private final ObjectMapper objectMapper;

    @Value("${jwt.token.access-expiration}")
    private long ACCESS_EXPIRATION;

    @Value("${jwt.token.refresh-expiration}")
    private long REFRESH_EXPIRATION;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest req, HttpServletResponse resp, Authentication authentication
    ) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        SiteUser siteUser = extractUserFromOAuth2User(oAuth2User);
        
        CustomUserDetails userDetails = new CustomUserDetails(siteUser);

        String username = userDetails.getUsername();
        if (username == null || username.isEmpty()) {
            throw new OAuth2AuthenticationException("username cannot be empty");
        }

        String accessToken = jwtUtil.createAccessToken(userDetails, ACCESS_EXPIRATION);
        String refreshToken = jwtUtil.createRefreshToken(userDetails, REFRESH_EXPIRATION);

        redisRepository.save(userDetails.getUsername(), refreshToken, REFRESH_EXPIRATION, TimeUnit.MILLISECONDS);

        resp.setHeader("Authorization", "Bearer " + accessToken);
        resp.setContentType("application/json;charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");

        KakaoLoginResponse kakaoLoginResponse = KakaoLoginResponse.builder()
                .id(siteUser.getId())
                .email(username + "@kakao.com")
                .name(siteUser.getName())
                .profileImg(siteUser.getProfileImg())
                .build();

//        AuthResponseUtil.success(
//                resp,
//                accessToken,
//                jwtUtil.setJwtCookie("refreshToken", refreshToken, REFRESH_EXPIRATION),
//                HttpServletResponse.SC_OK,
//                GenericResponse.of(true, 200, kakaoLoginResponse, "카카오 로그인에 성공하였습니다."),
//                objectMapper
//        );

        GenericResponse<KakaoLoginResponse> response = GenericResponse.ok(
            kakaoLoginResponse,
            "카카오 로그인에 성공하였습니다."
        );

        resp.getWriter().write(objectMapper.writeValueAsString(response));

        String userInfo = URLEncoder.encode(objectMapper.writeValueAsString(kakaoLoginResponse), StandardCharsets.UTF_8);
        String redirectUrl = String.format("%s/login?token=%s&user=%s",
                AppConfig.getSiteFrontUrl(),
                accessToken,
                userInfo
        );
        resp.sendRedirect(redirectUrl);

    }

    private SiteUser extractUserFromOAuth2User(OAuth2User oAuth2User) {
        try {
            Map<String, Object> attributes = oAuth2User.getAttributes();
            Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");
            String nickname = (String) properties.get("nickname");

            Long userId = ((Number) attributes.get("id")).longValue();

            if (nickname == null || nickname.isEmpty()) {
                throw new OAuth2AuthenticationException("nickname cannot be empty");
            }

            return SiteUser.builder()
                    .id(userId)
                    .name(nickname)
                    .email(nickname + "@kakao.com")
                    .kakaoId(String.valueOf(attributes.get("id")))
                    .profileImg((String) properties.get("profile_image"))
                    .password("")
                    .userRole(UserRole.ROLE_USER.toString())
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            throw new OAuth2AuthenticationException("카카오 사용자 정보 변환에 실패했습니다");
        }
    }

}

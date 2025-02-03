package com.backend.global.security.handler;

import com.backend.global.redis.repository.RedisRepository;
import com.backend.standard.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class OAuth2LoginSuccessHandlerTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private RedisRepository redisRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

    @Test
    @DisplayName("토큰 발급 테스트")
    void test1() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        OAuth2User oAuth2User = createMockOAuth2User();
        Authentication authentication = new OAuth2AuthenticationToken(
                oAuth2User,
                List.of(new SimpleGrantedAuthority("ROLE_USER")),
                "kakao"
        );

        when(jwtUtil.createAccessToken(any(), anyLong())).thenReturn("Bearer test.access.token");
        when(jwtUtil.createRefreshToken(any(), anyLong())).thenReturn("test.refresh.token");
        
        Cookie refreshTokenCookie = new Cookie("refreshToken", "test.refresh.token");
        when(jwtUtil.setJwtCookie(eq("refreshToken"), anyString(), anyLong()))
                .thenReturn(refreshTokenCookie);
            
        when(objectMapper.writeValueAsString(any()))
                .thenReturn("{\"success\":true,\"code\":\"200\",\"message\":\"카카오 로그인에 성공하였습니다.\"}");

        oAuth2LoginSuccessHandler.onAuthenticationSuccess(request, response, authentication);

        verify(redisRepository).save(anyString(), anyString(), anyLong(), any());
        assertEquals("Bearer test.access.token", response.getHeader("Authorization"));
        assertTrue(response.getCookie("refreshToken").getValue().contains("test.refresh.token"));
        assertEquals("application/json;charset=UTF-8", response.getContentType());
        assertEquals("UTF-8", response.getCharacterEncoding());
        assertTrue(response.getContentAsString().contains("success"));
    }

    private OAuth2User createMockOAuth2User() {
        Map<String, Object> attributes = new HashMap<>();
        Map<String, Object> properties = new HashMap<>();
        properties.put("nickname", "testUser");
        properties.put("profile_image", "test.jpg");
        attributes.put("id", 12345L);
        attributes.put("properties", properties);

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                attributes,
                "id"
        );
    }

}

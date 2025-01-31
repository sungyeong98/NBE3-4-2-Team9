package com.backend.global.security.oauth;

import com.backend.domain.user.entity.SiteUser;
import com.backend.domain.user.entity.UserRole;
import com.backend.domain.user.repository.UserRepository;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class CustomOAuth2UserServiceTest {

    @Mock
    private UserRepository userRepository;

    private CustomOAuth2UserService customOAuth2UserService;

    @BeforeEach
    void setUp() {
        customOAuth2UserService = new CustomOAuth2UserService(userRepository);
    }

    @Test
    @DisplayName("신규 사용자 저장 테스트")
    void saveNewUserTest() {
        SiteUser siteUser = SiteUser.builder()
                .name("testUser")
                .email("testUser@kakao.com")
                .kakaoId("12345")
                .profileImg("test.jpg")
                .password("")
                .userRole(UserRole.ROLE_USER.toString())
                .build();
                
        when(userRepository.findByKakaoId("12345")).thenReturn(Optional.empty());
        when(userRepository.save(any(SiteUser.class))).thenReturn(siteUser);

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("id", "12345");
        Map<String, Object> properties = new HashMap<>();
        properties.put("nickname", "testUser");
        properties.put("profile_image", "test.jpg");
        attributes.put("properties", properties);

        OAuthAttributes oAuthAttributes = OAuthAttributes.of("kakao", "id", attributes);

        SiteUser savedUser = customOAuth2UserService.saveOrUpdate(oAuthAttributes);

        assertNotNull(savedUser);
        assertEquals("testUser", savedUser.getName());
        assertEquals("12345", savedUser.getKakaoId());
        verify(userRepository).save(any(SiteUser.class));
    }

}

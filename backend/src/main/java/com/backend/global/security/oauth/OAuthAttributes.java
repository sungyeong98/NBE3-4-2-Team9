package com.backend.global.security.oauth;

import com.backend.domain.user.entity.SiteUser;
import com.backend.domain.user.entity.UserRole;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class OAuthAttributes {

    private String name;
    private String profileImg;
    private String kakaoId;
    private Map<String, Object> attributes;

    @Builder
    public OAuthAttributes(String name, String profileImg, String kakaoId, Map<String, Object> attributes) {
        this.name = name;
        this.profileImg = profileImg;
        this.kakaoId = kakaoId;
        this.attributes = attributes;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public static OAuthAttributes of(String registrationId, String userNameAttributeName, Map<String, Object> attributes) {
        if("kakao".equals(registrationId)) {
            return ofKakao(userNameAttributeName, attributes);
        }
        throw new OAuth2AuthenticationException("해당 소셜 로그인은 지원하지 않습니다.");
    }

    private static OAuthAttributes ofKakao(String userNameAttributeName, Map<String, Object> attributes) {
        Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");

        return OAuthAttributes.builder()
                .name((String) properties.get("nickname"))
                .profileImg((String) properties.get("profile_image"))
                .kakaoId(String.valueOf(attributes.get(userNameAttributeName)))
                .attributes(attributes)
                .build();
    }

    public SiteUser toEntity() {
        return SiteUser.builder()
                .name(name)
                .password("")
                .email(name + "@kakao.com")
                .profileImg(profileImg)
                .kakaoId(kakaoId)
                .userRole(UserRole.ROLE_USER.toString())
                .build();
    }

} 
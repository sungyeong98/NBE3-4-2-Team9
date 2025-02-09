package com.backend.standard.util;

import com.backend.global.security.custom.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtil {

    /**
     * 현재 인증된 사용자의 ID를 반환합니다.
     * 인증되지 않은 사용자의 경우 null을 반환합니다.
     * 상태 정보가 필요한 경우 이 메소드를 호출해 현재 사용자의 ID를 확인할 수 있습니다.
     *
     * @return 현재 사용자의 ID (인증되지 않은 경우 null)
     */
    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return null;  // 인증되지 않은 경우 null 반환
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomUserDetails) {
            return ((CustomUserDetails) principal).getId();
        }

        return null;  // 인증 정보에서 사용자 ID를 찾을 수 없는 경우 null 반환
    }
}
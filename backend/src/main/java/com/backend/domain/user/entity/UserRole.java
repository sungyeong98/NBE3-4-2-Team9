package com.backend.domain.user.entity;

import com.backend.global.exception.GlobalErrorCode;
import com.backend.global.exception.GlobalException;

public enum UserRole {

    ROLE_USER("일반 사용자"),
    ROLE_ADMIN("관리자");

    private final String description;

    UserRole(String description) {
        this.description = description;
    }

    public boolean isAdmin() {
        return this == ROLE_ADMIN;
    }

    public static UserRole defaultRole() {
        return ROLE_USER;
    }

    public static UserRole fromString(String role) {
        // "ROLE_" 접두어가 없는 경우 추가
        if (!role.startsWith("ROLE_")) {
            role = "ROLE_" + role;
        }

        // Enum 값과 비교
        for (UserRole userRole : UserRole.values()) {
            if (userRole.name().equalsIgnoreCase(role)) {
                return userRole;
            }
        }
        throw new GlobalException(GlobalErrorCode.UNAUTHORIZATION_USER);
    }
}

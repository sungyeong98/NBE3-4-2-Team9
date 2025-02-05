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

    // String을 Enum으로 변환
    public static UserRole fromString(String role) {
        for (UserRole userRole : UserRole.values()) {
            if (userRole.name().equalsIgnoreCase(role)) {
                return userRole;
            }
        }
        throw new GlobalException(GlobalErrorCode.UNAUTHORIZATION_USER);
    }
}

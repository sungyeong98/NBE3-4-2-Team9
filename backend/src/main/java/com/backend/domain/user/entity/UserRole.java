package com.backend.domain.user.entity;

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

}

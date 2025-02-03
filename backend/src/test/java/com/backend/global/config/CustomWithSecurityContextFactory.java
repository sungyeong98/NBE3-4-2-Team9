package com.backend.global.config;

import com.backend.domain.user.entity.SiteUser;
import com.backend.global.annotation.CustomWithMock;
import com.backend.global.security.custom.CustomUserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class CustomWithSecurityContextFactory implements WithSecurityContextFactory<CustomWithMock> {

    @Override
    public SecurityContext createSecurityContext(CustomWithMock annotation) {
        CustomUserDetails userDetails = new CustomUserDetails(SiteUser.builder()
                .id(annotation.id())
                .email(annotation.email())
                .password(annotation.password())
                .name(annotation.name())
                .userRole(annotation.role())
                .build());

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities())
        );

        return securityContext;
    }

}

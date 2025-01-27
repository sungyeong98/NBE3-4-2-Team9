package com.backend.domain.user.dto.request;

import com.backend.domain.user.entity.SiteUser;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class AdminLoginRequest {

    @NotBlank
    private String email;

    @NotBlank
    private String password;

    public AdminLoginRequest(SiteUser siteUser) {
        this.email = siteUser.getEmail();
        this.password = siteUser.getPassword();
    }

}

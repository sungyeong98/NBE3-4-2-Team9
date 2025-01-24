package com.backend.domain.user.dto.response;

import com.backend.domain.user.entity.SiteUser;
import lombok.Getter;

@Getter
public class UserGetProfileResponse {

    private String name;

    private String email;

    private String introduction;

    private String job;

    private String skill;

    public UserGetProfileResponse(SiteUser siteUser) {
        this.name = siteUser.getName();
        this.email = siteUser.getEmail();
        this.introduction = siteUser.getIntroduction();
        this.job = siteUser.getJob();
        this.skill = siteUser.getSkill();
    }

}

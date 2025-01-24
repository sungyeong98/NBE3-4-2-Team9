package com.backend.domain.user.dto.request;

import com.backend.domain.user.entity.SiteUser;
import lombok.Getter;

@Getter
public class UserModifyProfileRequest {

    private String introduction;

    private String job;

    private String skill;

    public UserModifyProfileRequest(SiteUser siteUser) {
        this.introduction = siteUser.getIntroduction();
        this.job = siteUser.getJob();
        this.skill = siteUser.getSkill();
    }

}

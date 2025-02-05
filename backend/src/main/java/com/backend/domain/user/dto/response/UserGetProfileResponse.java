package com.backend.domain.user.dto.response;

import com.backend.domain.jobskill.dto.JobSkillResponse;
import com.backend.domain.user.entity.SiteUser;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;

@Getter
public class UserGetProfileResponse {

    private final String name;

    private final String email;

    private final String introduction;

    private final String job;

    private final List<JobSkillResponse> jobSkills;

    private final String profileImg;

	public UserGetProfileResponse(SiteUser siteUser) {
		this.name = siteUser.getName();
		this.email = siteUser.getEmail();
		this.introduction = siteUser.getIntroduction();
		this.job = siteUser.getJob();
		this.jobSkills = siteUser.getJobSkills() != null ? siteUser.getJobSkills().stream()
			.map((j) -> JobSkillResponse.builder()
                .code(j.getCode())
                .name(j.getName())
                .build())
			.collect(Collectors.toList()) : null;
		this.profileImg = siteUser.getProfileImg();
	}

}

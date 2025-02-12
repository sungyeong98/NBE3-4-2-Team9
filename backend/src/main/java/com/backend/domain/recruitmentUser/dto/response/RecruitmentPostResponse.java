package com.backend.domain.recruitmentUser.dto.response;

import org.springframework.data.domain.Page;

import com.backend.domain.post.dto.PostPageResponse;
import com.backend.domain.recruitmentUser.entity.RecruitmentUserStatus;

public record RecruitmentPostResponse(
	RecruitmentUserStatus status,
	Page<PostPageResponse> postPageResponses
) {

}

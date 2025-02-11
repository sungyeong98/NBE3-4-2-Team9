package com.backend.domain.post.dto;

import com.querydsl.core.annotations.QueryProjection;
import java.time.ZonedDateTime;
import lombok.Builder;

@Builder
public record PostPageResponse(
	Long postId,
	String subject,
	String categoryName,
	String authorName,
	String authorProfileImage,
	Long commentCount,
	ZonedDateTime createdAt) {

	@QueryProjection
	public PostPageResponse {
	}
}

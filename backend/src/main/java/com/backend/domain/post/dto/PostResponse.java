package com.backend.domain.post.dto;

import com.querydsl.core.annotations.QueryProjection;
import java.time.ZonedDateTime;
import lombok.Builder;

@Builder
public record PostResponse(
	Long id,
	String subject,
	String content,
	Long categoryId,
	boolean isAuthor,
	String authorName,
	String authorImg,
	Long voterCount,
	boolean isVoter,
	ZonedDateTime createdAt

	/*// 모집 게시판 전용 필드
	@JsonInclude(value = Include.NON_NULL)
	Long jobPostingId,

	@JsonInclude(value = Include.NON_NULL)
	Integer numOfApplicants,

	@JsonInclude(value = Include.NON_NULL)
	RecruitmentStatus recruitmentStatus,

	@JsonInclude(value = Include.NON_NULL)
	Integer currentAcceptedCount*/
) {

	@QueryProjection
	public PostResponse {
	}
}

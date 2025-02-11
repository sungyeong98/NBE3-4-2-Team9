package com.backend.domain.post.dto;

import com.backend.domain.post.entity.RecruitmentStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.querydsl.core.annotations.QueryProjection;
import java.time.ZonedDateTime;
import lombok.Builder;

@Builder
public record PostResponse(
	Long id,
	String subject,
	String content,
	Long categoryId,
	Long jobPostingId,
	boolean isAuthor,
	String authorName,
	String authorImg,
	Long voterCount,
	boolean isVoter,
	ZonedDateTime createdAt,

	// 모집 게시판 전용 필드
	@JsonInclude(value = Include.NON_NULL)
	Integer numOfApplicants,

	@JsonInclude(value = Include.NON_NULL)
	RecruitmentStatus recruitmentStatus
) {

	@QueryProjection
	public PostResponse {
	}
}

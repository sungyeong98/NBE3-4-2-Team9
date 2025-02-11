package com.backend.domain.post.dto;

import com.backend.domain.post.entity.RecruitmentStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.time.ZonedDateTime;

import com.backend.domain.post.entity.Post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class PostResponse {

	private Long id;
	private String subject;
	private String content;
	private Long categoryId;
	private Long jobPostingId;
	private boolean isAuthor;
	private String authorName;
	private String authorImg;
	private ZonedDateTime createdAt;

	// 모집 게시판 전용 필드
	@JsonInclude(value = Include.NON_NULL)
	private Integer numOfApplicants;

	@JsonInclude(value = Include.NON_NULL)
	private RecruitmentStatus recruitmentStatus; // Enum -> String

    // Entity -> DTO 변환(Builder 활용)
    public static PostResponseDto fromEntity(Post post) {
        return PostResponseDto.builder()
                .id(post.getPostId())
                .subject(post.getSubject())
                .content(post.getContent())
                .categoryId(post.getCategoryId().getId())
                .jobPostingId(post.getJobId() != null ? post.getJobId().getId() : null)
                .createdAt(post.getCreatedAt())
                .authorName(post.getAuthor().getName())
                .authorImg(post.getAuthor().getProfileImg())
                .build();

    }
}

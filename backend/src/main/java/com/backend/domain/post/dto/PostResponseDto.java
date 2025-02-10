package com.backend.domain.post.dto;

import java.time.ZonedDateTime;

import com.backend.domain.post.entity.Post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class PostResponseDto {

    private Long id;
    private String subject;
    private String content;
    private Long categoryId;
    private Long jobPostingId;
    private boolean isAuthor;
    private String authorName;
    private String authorImg;
    private ZonedDateTime recruitmentClosingDate;
    private int numOfApplicants;
    private int currentUserCount;
    private ZonedDateTime createdAt;

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
                .recruitmentClosingDate(post.getRecruitmentClosingDate()) // 종료 시간 추가
                .numOfApplicants(post.getNumOfApplicants()) // 모집 인원 추가
                .currentUserCount(post.getCurrentUserCount()) // 현재 인원 추가
                .build();

    }
}

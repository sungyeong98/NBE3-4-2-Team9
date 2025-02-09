package com.backend.domain.post.dto;

import com.backend.domain.post.entity.Post;
import com.backend.domain.recruitmentUser.entity.RecruitmentUserStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // null 값도 포함하도록 설정
public class PostResponseDto {

    private Long id;
    private String subject;
    private String content;
    private Long categoryId;
    private Long jobPostingId;
    private Long authorId;
    private String authorName;
    private ZonedDateTime createdAt;
    private RecruitmentUserStatus status;

    // Entity -> DTO 변환(Builder 활용)
    public static PostResponseDto fromEntity(Post post, RecruitmentUserStatus status) {
        return PostResponseDto.builder()
                .id(post.getPostId())
                .subject(post.getSubject())
                .content(post.getContent())
                .categoryId(post.getCategoryId().getId())
                .jobPostingId(post.getJobId() != null ? post.getJobId().getId() : null)
                .createdAt(post.getCreatedAt())
                .status(status)
                .build();
    }
}

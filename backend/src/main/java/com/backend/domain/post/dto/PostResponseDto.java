package com.backend.domain.post.dto;

import java.time.ZonedDateTime;

import com.backend.domain.post.entity.Post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostResponseDto {

    private Long id;
    private String subject;
    private String content;
    private Long categoryId;
    private Long jobPostingId;
    private Long authorId;
    private String authorName;
    private String authorImg;
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
            .authorId(post.getAuthor().getId())
            .authorName(post.getAuthor().getName())
            .authorImg(post.getAuthor().getProfileImg())
            .build();

    }
}
package com.backend.domain.post.dto;

import java.time.ZonedDateTime;
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

}

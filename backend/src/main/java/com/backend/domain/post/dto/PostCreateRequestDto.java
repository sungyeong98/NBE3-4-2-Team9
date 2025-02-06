package com.backend.domain.post.dto;

import com.backend.domain.category.entity.Category;
import com.backend.domain.jobposting.entity.JobPosting;
import com.backend.domain.post.entity.Post;
import com.backend.domain.post.entity.RecruitmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostCreateRequestDto {

    private String subject;
    private String content;
    private Long categoryId;
    @Builder.Default // 기본값
    private Long jobPostingId = null; // 모집 게시판 아닐 경우 null

}


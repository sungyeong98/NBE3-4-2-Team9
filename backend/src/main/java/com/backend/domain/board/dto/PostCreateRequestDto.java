package com.backend.domain.board.dto;

import com.backend.domain.board.entity.Post;
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
    private String category; // 게시글 카테고리명
    private Long categoryId; // 카테고리 ID
    private Long jobId; // 채용 공고 ID

    // DTO -> Entity
    // TODO: category, jobposting 미구현, 구현 이후 다시 작업
//    public Post toEntity(Category categoryEntity, JobPosting jobPostingEntity){
//        return Post.builder()
//                .subject(this.subject)
//                .content(this.content)
//                .category(this.category)
//                .categoryId(this.categoryEntity)
//                .jobPosting(this.jobPostingEntity)
//                .build();
//    }
}


package com.backend.domain.board.dto;

import com.backend.domain.board.entity.Post;
import com.backend.domain.board.entity.PostType;
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
    private PostType postType; // 게시판 유형
    private Long categoryId;

    public Long getCategoryId(){
        return categoryId;
    }

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


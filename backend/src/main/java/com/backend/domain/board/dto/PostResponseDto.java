package com.backend.domain.board.dto;

import com.backend.domain.board.entity.Post;
import java.time.format.DateTimeFormatter;
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
    private String category;
    private Long categoryId;
    private Long jobId;
    private String formattedDate;

    // Entity -> DTO 변환(Builder 활용)
    public static PostResponseDto fromEntity(Post post){
        return PostResponseDto.builder()
                .id(post.getBoard_id())
                .subject(post.getSubject())
                .content(post.getContent())
                .category(post.getCategory())
            // TODO: category, jobposting 미구현, 구현 이후 다시 작업
//                .categoryId(post.getCategoryId().getId())
//                .jobId(post.getJobId().getId())
                .formattedDate(post.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                .build();

    }
}

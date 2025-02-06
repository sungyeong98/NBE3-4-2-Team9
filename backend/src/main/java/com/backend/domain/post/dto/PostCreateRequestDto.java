package com.backend.domain.post.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostCreateRequestDto {

    @NotBlank(message = "제목을 입력해 주세요.")
    private String subject;
    @NotBlank(message = "내용을 입력해 주세요.")
    private String content;g
    @NotNull(message = "카테고리를 선택해 주세요.")
    private Long categoryId;
    @Builder.Default // 기본값
    private Long jobPostingId = null; // 모집 게시판 아닐 경우 null

}


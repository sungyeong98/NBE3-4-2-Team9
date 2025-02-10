package com.backend.domain.comment.controller;

import com.backend.domain.comment.dto.request.CommentRequestDto;
import com.backend.domain.comment.dto.response.CommentCreateResponseDto;
import com.backend.domain.comment.service.CommentService;
import com.backend.domain.post.service.PostService;
import com.backend.global.response.GenericResponse;
import com.backend.global.security.custom.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts/{postId}/comments")
public class CommentController {

    private final CommentService commentService;
    private final PostService postService;

    @PostMapping
    public GenericResponse<CommentCreateResponseDto> createComment(
        @PathVariable("postId") Long postId,
        @Valid @RequestBody CommentRequestDto requestDto,
        @AuthenticationPrincipal CustomUserDetails user) {

        CommentCreateResponseDto commentResponseDto = commentService.createComment(requestDto, postId, user);

        return GenericResponse.of(
            true,
            HttpStatus.CREATED.value(),
             commentResponseDto,
            "댓글이 정상적으로 생성되었습니다."
        );
    }
}
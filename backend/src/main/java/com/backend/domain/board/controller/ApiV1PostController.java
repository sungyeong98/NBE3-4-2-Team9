package com.backend.domain.board.controller;

import com.backend.domain.board.dto.PostResponseDto;
import com.backend.domain.board.service.PostService;
import com.backend.global.response.GenericResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/posts")
@RestController
@RequiredArgsConstructor
public class ApiV1PostController {

    // PostService 주입
    private final PostService postService;

//     게시글 생성 (DTO 적용)
//     TODO: category, jobposting 미구현, 구현 이후 다시 작업
//    @PostMapping("/posts")
//    public GenericResponse<PostResponseDto> createPost
//    (@RequestBody PostCreateRequestDto responseDto){
//        PostResponseDto createdPost = postService.createPost(responseDto);
//        return GenericResponse.of();
//    }

    //     전체 게시글 조회 (DTO 적용) + 조건 없이 전체 글 조회, 카테고리, 정렬, 검색, 페이징
    @GetMapping
    public GenericResponse<Page<PostResponseDto>> getAllPosts(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "latest") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<PostResponseDto> posts = postService.getAllPosts(categoryId, keyword, sort,
                page, size);

        return GenericResponse.of(true, HttpStatus.OK.value(), posts);
    }

    // 특정 게시글 조회 (DTO 적용)
    @GetMapping("/{id}")
    public GenericResponse<PostResponseDto> getPostById(@PathVariable Long id) {

        PostResponseDto post = postService.getPostById(id);

        return GenericResponse.of(true, HttpStatus.OK.value(), post);
    }

    // 게사글 수정 (DTO 적용)
//    @PutMapping("/posts/{id}")
//    public GenericResponse<PostResponseDto> updatePost(@PathVariable Long id,
//    @RequestBody PostCreateRequestDto requestDto){
//        PostResponseDto updatedPost = postService.updatePost(id, requestDto);
//        return GenericResponse.of();
//    }

    // 게시글 삭제
//    @DeleteMapping("/posts/{id}")
//    public GenericResponse<Void> deletePost(@PathVariable Long id){
//        postService.deletePost(id);
//        return GenericResponse.noContent().build();
//    }
}

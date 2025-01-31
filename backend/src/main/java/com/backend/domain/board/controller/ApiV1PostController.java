package com.backend.domain.board.controller;

import com.backend.domain.board.dto.PostCreateRequestDto;
import com.backend.domain.board.dto.PostResponseDto;
import com.backend.domain.board.entity.Post;
import com.backend.domain.board.service.PostService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1")
@RestController
@RequiredArgsConstructor
public class ApiV1PostController {

    // PostService 주입
    private final PostService postService;

    // 게시글 생성 (DTO 적용)
    // TODO: category, jobposting 미구현, 구현 이후 다시 작업
//    @PostMapping("/posts")
//    public ResponseEntity<PostResponseDto> createPost(@RequestBody PostResponseDto responseDto){
//        PostResponseDto createdPost = postService.creatPost(responseDto);
//        return ResponseEntity.ok(createdPost);
//    }

    // 전체 게시글 조회 (DTO 적용)
    @GetMapping("/posts")
    public ResponseEntity<List<PostResponseDto>> getAllPosts(){
        List<PostResponseDto> posts = postService.getAllPosts();
        return ResponseEntity.ok(posts);
    }

    // 특정 게시글 조회 (DTO 적용)
    @GetMapping("/posts/{id}")
    public ResponseEntity <PostResponseDto> getPostById(@PathVariable Long id){
        PostResponseDto post = postService.getPostById(id);
        return ResponseEntity.ok(post);
    }

    // 게사글 수정 (DTO 적용)
    @PutMapping("/posts/{id}")
    public ResponseEntity<PostResponseDto> updatePost(@PathVariable Long id, @RequestBody PostCreateRequestDto requestDto){
        PostResponseDto updatedPost = postService.updatePost(id, requestDto);
        return ResponseEntity.ok(updatedPost);
    }

    // 게시글 삭제
    @DeleteMapping("/posts/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id){
        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }
}

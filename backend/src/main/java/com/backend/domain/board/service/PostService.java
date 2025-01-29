package com.backend.domain.board.service;

import com.backend.domain.board.entity.Post;
import com.backend.domain.board.repository.PostRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {

    // PostRepository 주입
    private final PostRepository postRepository;

    // 게시글 생성
    public Post creatPost(Post post){
        return postRepository.save(post);
    }

    // 게시글 전체 조회
    public List<Post> getAllPosts(){
        return postRepository.findAll();
    }

    // 게시글 상세 조회
    public Post getPostById(Long id){
        return postRepository.findById(id).orElseThrow(() ->
        new IllegalArgumentException("해당 게시글이 존재하지 않습니다. ID: " + id));
    }

    // 게시글 수정
    public Post updatePost(Long id, Post updatedPost){
        Post post = postRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("해당 게시물이 존재하지 않습니다. ID: "+ id));
        post.setSubject(updatedPost.getSubject());
        post.setContent(updatedPost.getContent());
        return postRepository.save(post); // 게시글 저장
    }

    // 게시글 삭제
    public void deletePost(Long id){
        postRepository.deleteById(id);
    }
}

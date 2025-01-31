package com.backend.domain.board.service;

import com.backend.domain.board.dto.PostCreateRequestDto;
import com.backend.domain.board.dto.PostResponseDto;
import com.backend.domain.board.entity.Post;
import com.backend.domain.board.repository.PostRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {

    // PostRepository, CategoryRepository, JobPostingRepository 주입
    private final PostRepository postRepository;
    // TODO: category, jobposting 미구현, 구현 이후 다시 작업
//    private final CategoryRepository categoryRepository;
//    private final JobPostingRepository jobPostingRepository;

    // 게시글 생성 (DTO 적용)
    // TODO: category, jobposting 미구현, 구현 이후 다시 작업
//    @Transactional
//    public PostResponseDto creatPost(PostCreateRequestDto requestDto){
//        // 필수값인 categoryId, jobId 기반 엔티티 조회
//        Category category = categoryRepository.findById(requestDto.getCategoryId())
//                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리입니다."));
//
//        JobPosting jobPosting = jobPostingRepository.findById(requestDto.getJobId())
//                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채용 정보입니다."));
//
//        // DTO -> Entity 변환
//        Post post = requestDto.toEntity(category, jobPosting);
//
//        // DB 저장
//        Post savedPost = postRepository.save(post);
//
//        return PostResponseDto.fromEntity(savedPost);
//    }

    // 게시글 전체 조회 (DTO 적용)
    public Page<PostResponseDto> getAllPosts(String category, String keyword, String sort, int page, int size){
        Pageable pageable;

        // 정렬 방식 설정
        if ("popular".equals(sort)){
            pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "viewCount"));
        } else {
            pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC,"createdAt"));
        }

        // Repository에서 검색, 페이징 처리된 게시글 리스트 반환
        Page<Post> posts = postRepository.findBySubjectContainingOrContentContaining(category, keyword, pageable);

        // Entity -> DTO 변환 후 반환
        return posts.map(PostResponseDto::fromEntity);
    }

    // 게시글 상세 조회 (DTO 적용)
    public PostResponseDto getPostById(Long id){
        Post post = postRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("해당 게시글이 존재하지 않습니다. ID: " + id));
        return PostResponseDto.fromEntity(post);
    }

    // 게시글 수정 (DTO 적용)
    @Transactional
    public PostResponseDto updatePost(Long id, PostCreateRequestDto requestDto){
        Post post = postRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("해당 게시물이 존재하지 않습니다. ID: "+ id));
        post.setSubject(requestDto.getSubject());
        post.setContent(requestDto.getContent());
        return PostResponseDto.fromEntity(post); // 게시글 저장
    }

    // 게시글 삭제
    @Transactional
    public void deletePost(Long id){
        Post post = postRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("해당 게시글이 존재하지 않습니다. ID: " + id));
        postRepository.delete(post);
    }
}

package com.backend.domain.board.service;

import com.backend.domain.board.dto.PostResponseDto;
import com.backend.domain.board.entity.Post;
import com.backend.domain.board.repository.PostRepository;

import com.backend.domain.category.repository.CategoryRepository;
import com.backend.global.exception.GlobalErrorCode;
import com.backend.global.exception.GlobalException;
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
    private final CategoryRepository categoryRepository;
//    private final JobPostingRepository jobPostingRepository;

//     게시글 생성 (DTO 적용)
//     TODO: category, jobposting 미구현, 구현 이후 다시 작업
//    @Transactional
//    public PostResponseDto createPost(PostCreateRequestDto requestDto){
//        // 필수값인 categoryId, jobId 기반 엔티티 조회
//        Category category = categoryRepository.findById(requestDto .getCategoryId())
//                .orElseThrow(() -> new GlobalException(GlobalErrorCode.CATEGORY_NOT_FOUND));
//
//        // DTO -> Entity 변환
//        Post post = Post.builder()
//                .subject(requestDto.getSubject())
//                .content(requestDto.getContent())
//                .postType(requestDto.getPostType())
//                .categoryId(category)
//                .build();
//
//        // DB 저장
//        Post savedPost = postRepository.save(post);
//
//        return PostResponseDto.fromEntity(savedPost);
//    }

    //         게시글 전체 조회 (postType → categoryId 변경)
    @Transactional(readOnly = true)
    public Page<PostResponseDto> getAllPosts(Long categoryId, String keyword, String sort,
            int page, int size) {
        Pageable pageable;

        // 정렬 방식 설정 (viewCount 또는 createdAt)
        if ("popular".equals(sort)) {
            pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "viewCount"));
        } else {
            pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        }

        // Repository에서 검색
        Page<Post> posts;
        if ((keyword == null || keyword.trim().isEmpty()) && categoryId == null) {
            // 전체 게시글 조회
            posts = postRepository.findAll(pageable);
        } else if (categoryId != null) {
            // 카테고리 & 검색어 필터링
            posts = postRepository.findByCategoryAndKeyword(categoryId, keyword, pageable);
        } else {
            // 키워드 필터링
            posts = postRepository.findByKeyword(keyword, pageable);
        }

        // Entity -> DTO 변환 후 반환
        return posts.map(PostResponseDto::fromEntity);
    }

    //  게시글 상세 조회 (유지)
    public PostResponseDto getPostById(Long id) {
        Post post = postRepository.findById(id).orElseThrow(() ->
                new GlobalException(GlobalErrorCode.POST_NOT_FOUND));
        return PostResponseDto.fromEntity(post);
    }
}

// 게시글 수정 (DTO 적용)
//    @Transactional
//    public PostResponseDto updatePost(Long id, PostCreateRequestDto requestDto) {
//        Post post = postRepository.findById(id).orElseThrow(() ->
//                new GlobalException(GlobalErrorCode.POST_NOT_FOUND));
//
//        post.updatePost(requestDto.getSubject(), requestDto.getContent());
//
//        return PostResponseDto.fromEntity(post); // 게시글 저장
//    }

// 게시글 삭제
//    @Transactional
//    public void deletePost(Long id) {
//        Post post = postRepository.findById(id).orElseThrow(() ->
//                new GlobalException(GlobalErrorCode.POST_NOT_FOUND));
//        postRepository.delete(post);
//    }

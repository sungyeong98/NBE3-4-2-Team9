package com.backend.domain.post.service;

import com.backend.domain.category.entity.Category;
import com.backend.domain.category.repository.CategoryRepository;
import com.backend.domain.jobposting.entity.JobPosting;
import com.backend.domain.jobposting.repository.JobPostingRepository;
import com.backend.domain.post.dto.PostCreateRequestDto;
import com.backend.domain.post.dto.PostResponseDto;
import com.backend.domain.post.entity.Post;
import com.backend.domain.post.entity.RecruitmentStatus;
import com.backend.domain.post.repository.PostRepository;
import com.backend.domain.recruitmentUser.entity.RecruitmentUserStatus;
import com.backend.domain.recruitmentUser.repository.RecruitmentUserRepository;
import com.backend.domain.user.entity.SiteUser;
import com.backend.global.exception.GlobalErrorCode;
import com.backend.global.exception.GlobalException;
import com.backend.standard.util.SecurityUtil;
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

    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;
    private final JobPostingRepository jobPostingRepository;
    private final RecruitmentUserRepository recruitmentUserRepository;

    //  게시글 생성
    @Transactional
    public PostResponseDto createPost(PostCreateRequestDto requestDto, SiteUser user) {

        // 1. 글 작성 전 카테고리 먼저 조회
        Category category = categoryRepository.findById(requestDto.getCategoryId())
                .orElseThrow(() -> new GlobalException(GlobalErrorCode.CATEGORY_NOT_FOUND));

        JobPosting jobPosting = null;

        // 모집 게시판일 때 jobpostingId가 필수
        if ("모집 게시판".equals(category.getName())) {
            if (requestDto.getJobPostingId() == null) {
                throw new GlobalException(GlobalErrorCode.JOB_POSTING_REQUIRED);
            }
            // JobPostingRepository를 이용하여 채용 공고 조회
            jobPosting = jobPostingRepository.findById(requestDto.getJobPostingId())
                    .orElseThrow(() -> new GlobalException(GlobalErrorCode.JOB_POSTING_REQUIRED));
        }

        // DTO -> Entity 변환
        Post post = Post.builder()
                .subject(requestDto.getSubject())
                .content(requestDto.getContent())
                .categoryId(category)
                .jobId(jobPosting)
                .recruitmentStatus(jobPosting != null ? RecruitmentStatus.OPEN : null)
                .author(user)
                .build();

        // DB 저장
        Post savedPost = postRepository.save(post);

        // RecruitmentUser에서 상태 가져오기
        RecruitmentUserStatus status = recruitmentUserRepository.findStatusByPostId(savedPost.getPostId());

        return PostResponseDto.fromEntity(savedPost, status);
    }

    // 게시글 전체 조회 (postType → categoryId 변경)
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

        // ✅ Entity -> DTO 변환 후 반환 (RecruitmentUserStatus 추가)
        return posts.map(post -> {
            // 현재 로그인한 사용자 정보 가져오기 (SecurityContext 사용)
            Long userId = SecurityUtil.getCurrentUserId(); // 현재 로그인한 사용자 ID 가져오는 로직 (구현 필요)

            // 해당 Post와 연결된 status 조회
            RecruitmentUserStatus status = recruitmentUserRepository.findStatusByPostIdAndUserId(
                            post.getPostId(), userId)
                    .orElse(null); // 없으면 null 처리

            return PostResponseDto.fromEntity(post, status);
        });
    }

        //  게시글 상세 조회 (유지)
        public PostResponseDto getPostById(Long id) {
            Post post = postRepository.findById(id).orElseThrow(() ->
                    new GlobalException(GlobalErrorCode.POST_NOT_FOUND));

            // 현재 로그인한 사용자의 ID를 가져오기
            Long userId = SecurityUtil.getCurrentUserId(); // 로그인된 사용자 ID

            // 해당 게시글에 대해 사용자의 상태를 조회
            RecruitmentUserStatus status = null;
            if (userId != null) {
                status = recruitmentUserRepository.findStatusByPostIdAndUserId(post.getPostId(), userId)
                        .orElse(null);  // 없으면 null 처리
            }

            return PostResponseDto.fromEntity(post, status);
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

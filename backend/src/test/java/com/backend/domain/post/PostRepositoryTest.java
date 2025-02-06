package com.backend.domain.post;

import static org.assertj.core.api.Assertions.assertThat;

import com.backend.domain.category.entity.Category;
import com.backend.domain.category.repository.CategoryRepository;
import com.backend.domain.post.entity.Post;
import com.backend.domain.post.repository.PostRepository;
import com.backend.domain.user.entity.SiteUser;
import com.backend.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Sql(scripts = {"/sql/init.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(scripts = {"/sql/delete.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
@Transactional
class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CategoryRepository categoryRepository;
    private Category freeBoardCategory;
    private Category recruitmentBoardCategory;

    @Autowired
    private UserRepository  userRepository;
    private SiteUser testUser;

    @BeforeEach
    void setUp() {
        testUser = userRepository.findByEmail("testEmail1@naver.com")
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 카테고리 가져오기
        freeBoardCategory = categoryRepository.findByName("자유 게시판").get(0);
        recruitmentBoardCategory = categoryRepository.findByName("모집 게시판").get(0);

    }

    @Test
    @DisplayName("카테고리 기준으로 게시글 조회")
    void testFindByCategoryId() {
        Pageable pageable = PageRequest.of(0, 10);

        // 자유 게시판 조회
        Page<Post> freePosts = postRepository.findAllByCategoryId(freeBoardCategory.getId(),
                pageable);
        assertThat(freePosts.getContent()).hasSize(1);

        // 모집 게시판 조회
        Page<Post> recruitmentPosts = postRepository.findAllByCategoryId(
                recruitmentBoardCategory.getId(), pageable);
        assertThat(recruitmentPosts.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("제목 or 내용으로 검색")
    void testFindByKeyword() {
        Pageable pageable = PageRequest.of(0, 10);

        // "test" 키워드가 포함된 모든 게시글 검색 (자유게시판, 모집게시판 모두 포함)
        Page<Post> posts = postRepository.findByKeyword("test", pageable);
        assertThat(posts.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("카테고리와 검색어를 기반으로 게시글 조회")
    void testFindByCategoryAndKeyword() {
        Pageable pageable = PageRequest.of(0, 10);

        // 자유게시판에서 "test" 포함된 게시글 검색
        Page<Post> posts_free1 = postRepository.findByCategoryAndKeyword(freeBoardCategory.getId(), "test",
                pageable);
        assertThat(posts_free1.getContent()).hasSize(1);

        // 자유게시판에서 "테스트" 포함된 게시글 검색 -> 없음
        Page<Post> posts_free2 = postRepository.findByCategoryAndKeyword(freeBoardCategory.getId(), "테스트",
                pageable);
        assertThat(posts_free2.getContent()).hasSize(0);

        // 모집게시판에서 "테스트" 포함된 게시글 검색
        Page<Post> posts_recruitment1 = postRepository.findByCategoryAndKeyword(recruitmentBoardCategory.getId(), "테스트",
                pageable);
        assertThat(posts_recruitment1.getContent()).hasSize(1);

        // 모집게시판에서 "test" 포함된 게시글 검색 -> 없음
        Page<Post> posts_recruitment2 = postRepository.findByCategoryAndKeyword(recruitmentBoardCategory.getId(), "test",
                pageable);
        assertThat(posts_recruitment2.getContent()).hasSize(0);

    }
}
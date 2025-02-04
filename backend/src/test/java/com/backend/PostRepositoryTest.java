package com.backend;

import com.backend.domain.board.entity.Post;
import com.backend.domain.board.entity.PostType;
import com.backend.domain.board.entity.RecruitmentStatus;
import com.backend.domain.board.repository.PostRepository;
import com.backend.domain.category.entity.Category;
import com.backend.domain.category.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Category testCategory;

    @BeforeEach
    void setUp() {
        testCategory = categoryRepository.save(new Category("테스트 카테고리"));
        postRepository.save(new Post("자유게시판 글1", "내용1", PostType.FREE, testCategory));
        postRepository.save(new Post("자유게시판 글2", "내용2", PostType.FREE, testCategory));

        // 모집 게시판은 recruitmentStatus를 명시적으로 설정
        postRepository.save(new Post("모집글1", "백엔드 개발자 모집", PostType.RECRUITMENT, testCategory));

    }


    @Test
    @DisplayName("postType 기준으로 게시글 조회")
    void testFindByPostType() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<Post> freePosts = postRepository.findAllByPostType(PostType.FREE, pageable);
        assertThat(freePosts.getContent()).hasSize(2);

        Page<Post> recruitmentPosts = postRepository.findAllByPostType(PostType.RECRUITMENT, pageable);
        assertThat(recruitmentPosts.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("제목 or 내용으로 검색")
    void testFindByKeyword() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<Post> posts = postRepository.findByKeywordAndPostType("모집", PostType.RECRUITMENT, pageable);
        assertThat(posts.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("카테고리와 postType으로 게시글 조회.")
    void testFindByCategoryAndPostType() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<Post> posts = postRepository.findByCategoryAndKeywordAndPostType(
                testCategory.getId(), null, PostType.FREE, pageable);
        assertThat(posts.getContent()).hasSize(2);
    }
}
package com.backend.domain.post.repository;

import com.backend.domain.post.dto.PostPageResponse;
import com.backend.domain.post.entity.Post;
import com.backend.domain.post.util.PostSearchCondition;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostRepository {

	/**
	 * @param postId 게시글 Id
	 * @return {@link Optional<Post>}
	 * @implSpec 게시글 ID로 단건 조회 메서드 입니다.
	 */
	Optional<Post> findById(Long postId);

		/**
	 * @param postId 게시글 Id
	 * @return {@link Optional<Post>}
	 * @implSpec 게시글 ID로 단건 조회 메서드 입니다. (fetch join 사용)
	 */
	Optional<Post> findByIdFetch(Long postId);

	/**
	 * @param post 게시글 엔티티
	 * @return {@link Post}
	 * @implSpec 게시글 저장 메서드 입니다.
	 */
	Post save(Post post);

	/**
	 * @param postId 게시글 Id
	 * @implSpec 게시글 삭제 메서드 입니다.
	 */
	void deleteById(Long postId);

	/**
	 *
	 * @param postSearchCondition 검색 조건 객체 {@link PostSearchCondition}
	 * @param pageable pageable
	 * @return
	 */
	Page<PostPageResponse> findAll(PostSearchCondition postSearchCondition, Pageable pageable);
}

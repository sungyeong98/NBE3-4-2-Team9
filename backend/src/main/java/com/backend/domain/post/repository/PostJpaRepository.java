package com.backend.domain.post.repository;

import com.backend.domain.post.entity.Post;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostJpaRepository extends JpaRepository<Post, Long> {

	@Query("select p from Post p join fetch p.author join fetch p.jobPosting where p.postId = :postId")
	Optional<Post> findByIdFetch(@Param("postId") Long postId);
}

package com.backend.domain.post.repository.recruitment;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.backend.domain.post.entity.RecruitmentPost;

import io.lettuce.core.dynamic.annotation.Param;

public interface RecruitmentPostJpaRepository extends JpaRepository<RecruitmentPost, Long> {
	@Query("""
SELECT rp 
FROM RecruitmentPost rp 
LEFT JOIN FETCH rp.author 
LEFT JOIN FETCH rp.jobPosting 
WHERE rp.postId = :postId 
""")
	Optional<RecruitmentPost> findByIdFetch(@Param("postId") Long postId);
}

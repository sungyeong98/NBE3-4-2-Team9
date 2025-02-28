package com.backend.domain.post.repository.recruitment;

import com.backend.domain.post.entity.RecruitmentPost;
import java.util.Optional;

public interface RecruitmentPostRepository {
	Optional<RecruitmentPost> findById(Long id);

	Optional<RecruitmentPost> findByIdFetch(Long id);

	RecruitmentPost save(RecruitmentPost recruitmentPost);
}

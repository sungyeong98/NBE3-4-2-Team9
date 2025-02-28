package com.backend.domain.post.repository.recruitment;

import com.backend.domain.post.entity.RecruitmentPost;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RecruitmentPostRepositoryImpl implements RecruitmentPostRepository {

	private final RecruitmentPostJpaRepository recruitmentPostJpaRepository;

	@Override
	public Optional<RecruitmentPost> findById(Long id) {
		return recruitmentPostJpaRepository.findById(id);
	}

	@Override
	public Optional<RecruitmentPost> findByIdFetch(Long id) {
		return recruitmentPostJpaRepository.findByIdFetch(id);
	}

	@Override
	public RecruitmentPost save(RecruitmentPost recruitmentPost) {
		return recruitmentPostJpaRepository.save(recruitmentPost);
	}
}

package com.backend.domain.voter.repository;

import com.backend.domain.voter.domain.VoterType;
import com.backend.domain.voter.entity.Voter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class VoterRepositoryImpl implements VoterRepository {

	private final VoterJpaRepository voterJpaRepository;

	@Override
	public Voter save(Voter voter) {
		return voterJpaRepository.save(voter);
	}

	@Override
	public boolean existsByJobPostingId(Long siteUserId, Long jobPostingId, VoterType voterType) {
		return voterJpaRepository.existsBySiteUserIdAndJobPostingIdAndVoterType(siteUserId,
			jobPostingId,
			voterType);
	}

	@Override
	public boolean existsByPostId(Long siteUserId, Long postId, VoterType voterType) {
		return voterJpaRepository.existsBySiteUserIdAndPostPostIdAndVoterType(siteUserId,
			postId,
			voterType);
	}

	@Override
	public void deleteByJobPostingId(Long jobPostingId) {
		voterJpaRepository.deleteByJobPostingId(jobPostingId);
	}

	@Override
	public void deleteByPostId(Long postId) {
		voterJpaRepository.deleteByPostPostId(postId);
	}
}

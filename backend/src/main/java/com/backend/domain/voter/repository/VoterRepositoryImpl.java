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
		return voterJpaRepository.existsByAndSiteUserIdAndJobPostingIdAndVoterType(siteUserId,
			jobPostingId,
			voterType);
	}
}

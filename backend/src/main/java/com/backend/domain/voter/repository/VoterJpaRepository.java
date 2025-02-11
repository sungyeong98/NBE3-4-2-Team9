package com.backend.domain.voter.repository;

import com.backend.domain.voter.domain.VoterType;
import com.backend.domain.voter.entity.Voter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoterJpaRepository extends JpaRepository<Voter, Long> {
	boolean existsByAndSiteUserIdAndJobPostingIdAndVoterType(Long siteUserId, Long jobPostingId, VoterType voterType);
//	boolean existsByAndSiteUserIdAndPostIdAndVoterType(Long siteUserId, Long postId, VoterType voterType);
	void deleteByJobPostingId(Long jobPostingId);
//	void deleteByPostId(Long postId);
}
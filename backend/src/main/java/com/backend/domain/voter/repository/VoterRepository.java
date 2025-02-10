package com.backend.domain.voter.repository;

import com.backend.domain.voter.domain.VoterType;
import com.backend.domain.voter.entity.Voter;

/**
 * VoterRepository
 * <p>Voter 리포지토리 입니다.</p>
 *
 * @author Kim Dong O
 */
public interface VoterRepository {

	/**
	 * @param voter Like 객체
	 * @return {@link Voter}
	 * @implSpec Voter 저장 메서드 입니다.
	 */
	Voter save(Voter voter);

	/**
	 * @param siteUserId siteUserId
	 * @param jobPostingId jobPostingId
	 * @param voterType voterType {@link VoterType}
	 * @return {@link Boolean} 데이터 존재할 시 true, 존재하지 않을 때 false
	 * @implSpec Voter exists 메서드 입니다.
	 */
	boolean existsByJobPostingId(Long siteUserId, Long jobPostingId, VoterType voterType);
}

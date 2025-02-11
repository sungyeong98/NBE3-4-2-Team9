package com.backend.domain.voter.service;

import com.backend.domain.jobposting.entity.JobPosting;
import com.backend.domain.jobposting.repository.JobPostingRepository;
import com.backend.domain.user.entity.SiteUser;
import com.backend.domain.voter.domain.VoterType;
import com.backend.domain.voter.dto.VoterCreateResponse;
import com.backend.domain.voter.entity.Voter;
import com.backend.domain.voter.repository.VoterRepository;
import com.backend.global.exception.GlobalErrorCode;
import com.backend.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * VoterService
 * <p>추천 서비스 입니다.</p>
 *
 * @author Kim Dong O
 */
@Service
@RequiredArgsConstructor
public class VoterService {

	private final VoterRepository voterRepository;
	private final JobPostingRepository jobPostingRepository;

	/**
	 * 추천 저장 메서드 입니다.
	 *
	 * @param siteUser  로그인 유저
	 * @param targetId  추천 타겟 ID
	 * @param voterType {@link VoterType} 추천 타입
	 * @return {@link VoterCreateResponse}
	 * @throws GlobalException 이미 추천 저장이 되어 있을 때 또는 지원하지 않는 VoterType 일 때 발생
	 */
	public VoterCreateResponse save(SiteUser siteUser, Long targetId, VoterType voterType) {
		boolean result = existsCheck(siteUser.getId(), targetId, voterType);

		if (result) {
			throw new GlobalException(GlobalErrorCode.VOTER_ALREADY);
		}

		switch (voterType) {
			case JOB_POSTING -> {

				JobPosting jobPosting = JobPosting.builder()
					.id(targetId)
					.build();

				Voter saveVoter = Voter.builder()
					.siteUser(siteUser)
					.jobPosting(jobPosting)
					.voterType(voterType)
					.build();

				voterRepository.save(saveVoter).getId();
			}
			case POST -> {
				//TODO Post 추가시 로직 구현 예정
			}
			default -> throw new GlobalException(GlobalErrorCode.NOT_SUPPORT_TYPE);
		}

		return VoterCreateResponse.builder()
			.targetId(targetId)
			.voterType(voterType)
			.build();
	}

	/**
	 * 특정 targetId에 추천을 눌렀는지 체크합니다.
	 * <p>
	 * 주어진 targetId에 추천을 눌렀는지 체크합니다.
	 * <br> 존재하지 않을 경우 예외를 발생시킵니다.
	 * </p>
	 *
	 * @param siteUserId siteUserId
	 * @param targetId   targetId
	 * @param voterType  검사할 타입
	 * @throws GlobalException 데이터가 존재하지 않을 경우 발생
	 */
	private boolean existsCheck(Long siteUserId, Long targetId, VoterType voterType) {
		boolean result = false;

		switch (voterType) {
			case JOB_POSTING -> result = voterRepository.
				existsByJobPostingId(siteUserId, targetId, voterType);
			/*case POST -> result = voterRepository
				.existsByPostId(siteUserId, targetId, voterType); //TODO 추후 변경 예정*/
		}

		return result;
	}

	/**
	 * 추천 삭제 메서드 입니다.
	 *
	 * @param voterType 추천 타입
	 * @param targetId 타겟 ID
	 * @param siteUser 로그인한 회원
	 * @throws GlobalException 데이터가 존재하지 않을 경우 발생
	 */
	@Transactional
	public void delete(VoterType voterType, Long targetId, SiteUser siteUser) {
		if (voterType == null) {
			throw new GlobalException(GlobalErrorCode.NOT_SUPPORT_TYPE);
		}

		boolean voterExists = existsCheck(siteUser.getId(), targetId, voterType);

		//데이터가 없을 때
		if (!voterExists) {
			throw new GlobalException(GlobalErrorCode.VOTER_NOT_FOUND);
		}

		switch (voterType) {
			case JOB_POSTING -> voterRepository.deleteByJobPostingId(targetId);
//			case POST -> voterRepository.deleteByPostId(targetId);
		}

	}
}

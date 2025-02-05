package com.backend.domain.jobposting.repository;

import com.backend.domain.jobposting.entity.JobPosting;
import java.util.List;
import java.util.Optional;

/**
 * JobPostingRepository
 * <p>JobPosting 리포지토리 입니다.</p>
 *
 * @author Kim Dong O
 */
public interface JobPostingRepository {

	/**
	 * @param id JobPosting id
	 * @return {@link Optional<JobPosting>}
	 * @implSpec Id 값으로 조회 메서드 입니다.
	 */
	Optional<JobPosting> findById(Long id);

	/**
	 * @param jobPosting JobPosting 객체
	 * @return {@link JobPosting}
	 * @implSpec JobPosting 저장 메서드 입니다.
	 */
	JobPosting save(JobPosting jobPosting);

	/**
	 * @return {@link List<JobPosting>}
	 * @implSpec JobPosting 전체 조회 메서드 입니다.
	 */
	List<JobPosting> findAll();

	void saveAll(List<JobPosting> publicDataList);

    boolean existsById(Long id);
}

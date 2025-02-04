package com.backend.domain.jobposting.repository;

import com.backend.domain.jobposting.entity.JobPosting;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JobPostingRepositoryImpl implements JobPostingRepository {

	private final JobPostingJpaRepository jobPostingJpaRepository;

	@Override
	public Optional<JobPosting> findById(Long id) {
		return jobPostingJpaRepository.findById(id);
	}

	@Override
	public JobPosting save(JobPosting jobPosting) {
		return jobPostingJpaRepository.save(jobPosting);
	}

	@Override
	public List<JobPosting> findAll() {
		return jobPostingJpaRepository.findAll();
	}

	@Override
	public void saveAll(List<JobPosting> publicDataList) {}
}

package com.backend.domain.jobposting.controller;

import com.backend.domain.jobposting.dto.JobPostingPageResponse;
import com.backend.domain.jobposting.service.JobPostingService;
import com.backend.domain.jobposting.util.JobPostingSearchCondition;
import com.backend.global.response.GenericResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ApiV1JobPostingController
 * <p>채용 공고 컨트롤러 입니다. <br>
 * 경로: /api/v1/job-posting</p>
 *
 * @author Kim Dong O
 */
@RestController
@RequestMapping("/api/v1/job-posting")
@RequiredArgsConstructor
@Slf4j
public class ApiV1JobPostingController {

	private final JobPostingService jobPostingService;

	/**
	 * 전체 조회 메서드 입니다.
	 *
	 * @param jobPostingSearchCondition
	 * @return {@link GenericResponse<Page<JobPostingPageResponse>>}
	 */
	@GetMapping
	public GenericResponse<Page<JobPostingPageResponse>> findAll(
		@Validated JobPostingSearchCondition jobPostingSearchCondition) {

		Page<JobPostingPageResponse> findAll = jobPostingService.findAll(jobPostingSearchCondition);

		return GenericResponse.of(true, HttpStatus.OK.value(), findAll);
	}
}

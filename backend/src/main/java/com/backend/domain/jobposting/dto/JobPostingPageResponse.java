package com.backend.domain.jobposting.dto;

import com.backend.domain.jobposting.entity.ExperienceLevel;
import com.backend.domain.jobposting.entity.JobPostingStatus;
import com.backend.domain.jobposting.entity.RequireEducate;
import com.backend.domain.jobposting.entity.Salary;
import com.querydsl.core.annotations.QueryProjection;
import java.time.ZonedDateTime;
import lombok.Builder;

/**
 * JobPostingPageResponse
 * <p>페이징 조회시 응답할 객체 입니다.</p>
 *
 * @param id               id
 * @param subject          제목
 * @param openDate         공개 날짜
 * @param closeDate        마감 날짜
 * @param experienceLevel  직무 경력
 * @param requireEducate   학력
 * @param jobPostingStatus 공고 상태
 * @param salary           연봉
 * @param applyCnt         지원자 수
 * @author Kim Dong O
 */
@Builder
public record JobPostingPageResponse(
	Long id,
	String subject, //제목

	ZonedDateTime openDate, //공개 날짜
	ZonedDateTime closeDate, //마감 날짜

	ExperienceLevel experienceLevel, //직무 경력

	RequireEducate requireEducate, //학력

	JobPostingStatus jobPostingStatus, //공고 상태

	Salary salary, //연봉

	Long applyCnt //지원자 수
) {

	@QueryProjection
	public JobPostingPageResponse {
	}
}
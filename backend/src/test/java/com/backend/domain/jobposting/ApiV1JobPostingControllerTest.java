package com.backend.domain.jobposting;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.backend.domain.jobposting.controller.ApiV1JobPostingController;
import com.backend.domain.jobposting.entity.ExperienceLevel;
import com.backend.domain.jobposting.entity.JobPosting;
import com.backend.domain.jobposting.entity.JobPostingStatus;
import com.backend.domain.jobposting.entity.RequireEducate;
import com.backend.domain.jobposting.entity.Salary;
import com.backend.domain.jobposting.repository.JobPostingRepository;
import com.backend.global.exception.GlobalErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.ZonedDateTime;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;


@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Slf4j
public class ApiV1JobPostingControllerTest {

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	ApiV1JobPostingController apiV1JobPostingController;

	@Autowired
	JobPostingRepository jobPostingRepository;

	@Autowired
	MockMvc mockMvc;

	@BeforeEach
	@Transactional
	void setUp() {
		ExperienceLevel experienceLevel1 = ExperienceLevel.builder()
			.name("신입")
			.code(1)
			.build();

		ExperienceLevel experienceLevel2 = ExperienceLevel.builder()
			.name("경력")
			.code(2)
			.build();

		Salary salaryCode9 = Salary.builder()
			.code(9)
			.name("2,600만원 이상")
			.build();

		Salary salaryCode22 = Salary.builder()
			.code(22)
			.name("1억원 이상")
			.build();

		Salary salaryCode99 = Salary.builder()
			.code(99)
			.name("면접 후 결정")
			.build();

		RequireEducate requireEducateCode0 = RequireEducate.builder()
			.code(0)
			.name("학력무관")
			.build();

		IntStream.range(0, 13).forEach((i) -> {
			JobPosting jobPosting = JobPosting.builder()
				.subject("testSubject")
				.jobPostingStatus(JobPostingStatus.ACTIVE)
				.postDate(ZonedDateTime.now())
				.openDate(ZonedDateTime.now())
				.closeDate(ZonedDateTime.now().plusDays(1))
				.companyLink("test")
				.companyName("test")
				.experienceLevel(experienceLevel1)
				.jobSkillList(null)
				.requireEducate(requireEducateCode0)
				.salary(salaryCode22)
				.url("test")
				.applyCnt((long) i)
				.build();

			jobPostingRepository.save(jobPosting);
		});

		IntStream.range(0, 13).forEach((i) -> {
			JobPosting jobPosting = JobPosting.builder()
				.subject("testSubject")
				.jobPostingStatus(JobPostingStatus.ACTIVE)
				.postDate(ZonedDateTime.now())
				.openDate(ZonedDateTime.now())
				.closeDate(ZonedDateTime.now().plusDays(1))
				.companyLink("test")
				.companyName("test")
				.experienceLevel(experienceLevel2)
				.jobSkillList(null)
				.requireEducate(requireEducateCode0)
				.salary(salaryCode99)
				.url("test")
				.applyCnt((long) i)
				.build();

			jobPostingRepository.save(jobPosting);
		});

		IntStream.range(0, 3).forEach((i) -> {
			JobPosting jobPosting = JobPosting.builder()
				.subject("testSubject")
				.jobPostingStatus(JobPostingStatus.ACTIVE)
				.postDate(ZonedDateTime.now())
				.openDate(ZonedDateTime.now())
				.closeDate(ZonedDateTime.now().plusDays(1))
				.companyLink("test")
				.companyName("test")
				.experienceLevel(experienceLevel2)
				.jobSkillList(null)
				.requireEducate(requireEducateCode0)
				.salary(salaryCode9)
				.url("test")
				.applyCnt((long) i)
				.build();

			jobPostingRepository.save(jobPosting);
		});

	}

	@DisplayName("기본 페이징 조회 성공 테스트")
	@Test
	void findAll_success() throws Exception {
		//when
		ResultActions resultActions = mockMvc.perform(get("/api/v1/job-posting")
			.contentType(MediaType.APPLICATION_JSON));

		//then
		resultActions.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.code").value(200))
			.andExpect(jsonPath("$.data.content.length()").value(10))
			.andExpect(jsonPath("$.data.content").exists())
			.andExpect(jsonPath("$.data.totalPages").value(3));
	}

	@DisplayName("페이징 경력 조건 신입 조회 성공 테스트")
	@Test
	void findAll_experience_level_1_success() throws Exception {
		//when
		ResultActions resultActions = mockMvc.perform(get("/api/v1/job-posting")
				.queryParam("experienceLevel", "1")
			.contentType(MediaType.APPLICATION_JSON));

		//then
		resultActions.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.code").value(200))
			.andExpect(jsonPath("$.data.content.length()").value(10))
			.andExpect(jsonPath("$.data.content").exists())
			.andExpect(jsonPath("$.data.content[2].experienceLevel.code").value(1))
			.andExpect(jsonPath("$.data.totalPages").value(2));
	}

	@DisplayName("페이징 경력 조건 조회 성공 테스트")
	@Test
	void findAll_experience_level_2_success() throws Exception {
		//when
		ResultActions resultActions = mockMvc.perform(get("/api/v1/job-posting")
				.queryParam("experienceLevel", "2")
			.contentType(MediaType.APPLICATION_JSON));

		//then
		resultActions.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.code").value(200))
			.andExpect(jsonPath("$.data.content.length()").value(10))
			.andExpect(jsonPath("$.data.content").exists())
			.andExpect(jsonPath("$.data.content[2].experienceLevel.code").value(2))
			.andExpect(jsonPath("$.data.totalPages").value(2));
	}

	@DisplayName("페이징 연봉 조건 조회 성공 테스트")
	@Test
	void findAll_salary_code_99_success() throws Exception {
		//when
		ResultActions resultActions = mockMvc.perform(get("/api/v1/job-posting")
				.queryParam("salaryCode", "99")
			.contentType(MediaType.APPLICATION_JSON));

		//then
		resultActions.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.code").value(200))
			.andExpect(jsonPath("$.data.content.length()").value(10))
			.andExpect(jsonPath("$.data.content").exists())
			.andExpect(jsonPath("$.data.content[2].salary.code").value(99))
			.andExpect(jsonPath("$.data.totalPages").value(2));
	}

	@DisplayName("페이징 조회 페이지 사이즈 음수 실패 테스트")
	@Test
	void findAll_page_size_negative_fail() throws Exception {
		//when
		ResultActions resultActions = mockMvc.perform(get("/api/v1/job-posting")
				.queryParam("pageSize", "-1")
			.contentType(MediaType.APPLICATION_JSON));

		//then
		resultActions.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.code").value(GlobalErrorCode.NOT_VALID.getCode()))
			.andExpect(jsonPath("$.data[0].field").value("pageSize"))
			.andExpect(jsonPath("$.message").value(GlobalErrorCode.NOT_VALID.getMessage()))
			.andDo(print());
	}

}

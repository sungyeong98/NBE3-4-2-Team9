package com.backend.domain.jobposting;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.backend.domain.jobposting.repository.JobPostingRepository;
import com.backend.global.exception.GlobalErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;


@SpringBootTest
@Sql(scripts = {"/sql/init.sql"}, executionPhase = ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(scripts = {"/sql/delete.sql"}, executionPhase = ExecutionPhase.AFTER_TEST_CLASS)
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class ApiV1JobPostingControllerTest {

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	JobPostingRepository jobPostingRepository;

	@Autowired
	MockMvc mockMvc;

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
			.andExpect(jsonPath("$.data.totalPages").value(2));
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
			.andExpect(jsonPath("$.data.content.length()").value(7))
			.andExpect(jsonPath("$.data.content").exists())
			.andExpect(jsonPath("$.data.content[2].experienceLevel.code").value(1))
			.andExpect(jsonPath("$.data.totalPages").value(1));
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
			.andExpect(jsonPath("$.data.content.length()").value(5))
			.andExpect(jsonPath("$.data.content").exists())
			.andExpect(jsonPath("$.data.content[2].experienceLevel.code").value(2))
			.andExpect(jsonPath("$.data.totalPages").value(1));
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
			.andExpect(jsonPath("$.data.content.length()").value(7))
			.andExpect(jsonPath("$.data.content").exists())
			.andExpect(jsonPath("$.data.content[2].salary.code").value(99))
			.andExpect(jsonPath("$.data.totalPages").value(1));
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

	@DisplayName("페이징 조회 페이지 번호 음수 실패 테스트")
	@Test
	void findAll_page_num_negative_fail() throws Exception {
		//when
		ResultActions resultActions = mockMvc.perform(get("/api/v1/job-posting")
				.queryParam("pageNum", "-1")
			.contentType(MediaType.APPLICATION_JSON));

		//then
		resultActions.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.code").value(GlobalErrorCode.NOT_VALID.getCode()))
			.andExpect(jsonPath("$.data[0].field").value("pageNum"))
			.andExpect(jsonPath("$.message").value(GlobalErrorCode.NOT_VALID.getMessage()))
			.andDo(print());
	}

}
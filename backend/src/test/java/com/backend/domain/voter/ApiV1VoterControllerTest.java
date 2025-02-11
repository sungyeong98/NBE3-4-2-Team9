package com.backend.domain.voter;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.backend.domain.jobposting.entity.JobPosting;
import com.backend.domain.user.entity.SiteUser;
import com.backend.domain.user.repository.UserRepository;
import com.backend.domain.voter.domain.VoterType;
import com.backend.domain.voter.dto.VoterCreateRequest;
import com.backend.domain.voter.entity.Voter;
import com.backend.domain.voter.repository.VoterRepository;
import com.backend.global.exception.GlobalErrorCode;
import com.backend.global.security.custom.CustomUserDetails;
import com.backend.standard.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest
@ActiveProfiles("test")
@Sql(scripts = {"/sql/init.sql"}, executionPhase = ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(scripts = {"/sql/delete.sql"}, executionPhase = ExecutionPhase.AFTER_TEST_CLASS)
@AutoConfigureMockMvc
@TestInstance(Lifecycle.PER_CLASS)
@Slf4j
public class ApiV1VoterControllerTest {

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	MockMvc mockMvc;

	@Autowired
	JwtUtil jwtUtil;

	@Autowired
	UserRepository userRepository;

	@Autowired
	VoterRepository voterRepository;

	@Value("${jwt.token.access-expiration}")
	long accessExpiration;

	String accessToken1;
	String accessToken2;

	@BeforeAll
	void setUp() {
		SiteUser givenSiteUser1 = userRepository.findByEmail("testEmail1@naver.com").get();
		CustomUserDetails givenCustomUserDetails1 = new CustomUserDetails(givenSiteUser1);
		accessToken1 = jwtUtil.createAccessToken(givenCustomUserDetails1, accessExpiration);

		SiteUser givenSiteUser2 = userRepository.findByEmail("testEmail2@naver.com").get();
		CustomUserDetails givenCustomUserDetails2 = new CustomUserDetails(givenSiteUser2);
		accessToken2 = jwtUtil.createAccessToken(givenCustomUserDetails2, accessExpiration);
	}

	@DisplayName("채용 공고 추천 등록 성공 테스트")
	@Order(1)
	@Test
	void save_job_posting_voter_success() throws Exception {
		//given
		VoterCreateRequest givenRequest = VoterCreateRequest.builder()
			.voterType(VoterType.JOB_POSTING)
			.targetId(4L)
			.build();

		//when
		ResultActions resultActions = mockMvc.perform(post("/api/v1/voter")
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + accessToken1)
				.content(objectMapper.writeValueAsString(givenRequest)))
			.andExpect(status().isCreated());

		//then
		resultActions
			.andExpect(jsonPath("$.code").value(201))
			.andExpect(jsonPath("$.data.targetId").value(4))
			.andExpect(jsonPath("$.data.voterType").value(givenRequest.voterType().toString()));
	}

	@DisplayName("채용 공고 추천이 이미 존재할 때 실패 테스트")
	@Test
	void save_job_posting_voter_fail() throws Exception {
		//given
		VoterCreateRequest givenRequest = VoterCreateRequest.builder()
			.voterType(VoterType.JOB_POSTING)
			.targetId(1L)
			.build();

		SiteUser givenSiteUser1 = userRepository.findByEmail("testEmail1@naver.com").get();

		voterRepository.save(Voter.builder()
			.siteUser(givenSiteUser1)
			.voterType(VoterType.JOB_POSTING)
			.jobPosting(JobPosting.builder()
				.id(1L)
				.build())
			.build());

		//when
		ResultActions resultActions = mockMvc.perform(post("/api/v1/voter")
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + accessToken1)
				.content(objectMapper.writeValueAsString(givenRequest)))
			.andExpect(status().isBadRequest());

		//then
		resultActions
			.andExpect(jsonPath("$.code").value(GlobalErrorCode.VOTER_ALREADY.getCode()))
			.andExpect(jsonPath("$.message").value(GlobalErrorCode.VOTER_ALREADY.getMessage()));
	}

	@DisplayName("추천 저장시 targetId가 null일 때 실패 테스트")
	@Test
	void save_voter_target_id_null_fail() throws Exception {
		//given
		VoterCreateRequest givenRequest = VoterCreateRequest.builder()
			.voterType(VoterType.JOB_POSTING)
			.build();

		//when
		ResultActions resultActions = mockMvc.perform(post("/api/v1/voter")
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + accessToken2)
				.content(objectMapper.writeValueAsString(givenRequest)))
			.andExpect(status().isBadRequest());

		//then
		resultActions
			.andExpect(jsonPath("$.code").value(GlobalErrorCode.NOT_VALID.getCode()))
			.andExpect(jsonPath("$.message").value(GlobalErrorCode.NOT_VALID.getMessage()));
	}

	@DisplayName("추천 저장시 voterType null일 때 실패 테스트")
	@Test
	void save_voter_voter_type_null_fail() throws Exception {
		//given
		VoterCreateRequest givenRequest = VoterCreateRequest.builder()
			.targetId(1L)
			.build();

		//when
		ResultActions resultActions = mockMvc.perform(post("/api/v1/voter")
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + accessToken2)
				.content(objectMapper.writeValueAsString(givenRequest)))
			.andExpect(status().isBadRequest());

		//then
		resultActions
			.andExpect(jsonPath("$.code").value(GlobalErrorCode.NOT_VALID.getCode()))
			.andExpect(jsonPath("$.message").value(GlobalErrorCode.NOT_VALID.getMessage()));
	}

	@DisplayName("추천 저장시 voterType이 존재하지 않는 값일 때 실패 테스트")
	@Test
	void save_voter_voter_type_not_valid_fail() throws Exception {
		//given
		Map<String, Object> param = new HashMap<>();
		param.put("targetId", 1L);
		param.put("voterType", "testVoter");

		//when
		ResultActions resultActions = mockMvc.perform(post("/api/v1/voter")
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + accessToken2)
				.content(objectMapper.writeValueAsString(param)))
			.andExpect(status().isBadRequest());

		//then
		resultActions
			.andExpect(jsonPath("$.code").value(GlobalErrorCode.NOT_VALID.getCode()))
			.andExpect(jsonPath("$.message").value(GlobalErrorCode.NOT_VALID.getMessage()));
	}

	@DisplayName("채용 공고 추천 삭제 성공 테스트")
	@Test
	void delete_voter_job_posting_success() throws Exception {
		//given
		VoterCreateRequest givenRequest = VoterCreateRequest.builder()
			.voterType(VoterType.JOB_POSTING)
			.targetId(1L)
			.build();

		//when
		ResultActions resultActions = mockMvc
			.perform(delete("/api/v1/voter/{targetId}?voterType=job_posting", 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + accessToken1));

		//then
		resultActions
			.andExpect(status().isOk());
	}

	@DisplayName("채용 공고 추천 삭제시 지원하지 않는 타입일 때 실패 테스트")
	@Test
	void delete_voter_job_posting_not_support_fail() throws Exception {
		//given
		VoterCreateRequest givenRequest = VoterCreateRequest.builder()
			.voterType(VoterType.JOB_POSTING)
			.targetId(1L)
			.build();

		//when
		ResultActions resultActions = mockMvc
			.perform(delete("/api/v1/voter/{targetId}?voterType=test", 4L)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + accessToken1));

		//then
		resultActions
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value(GlobalErrorCode.NOT_SUPPORT_TYPE.getCode()))
			.andExpect(jsonPath("$.message").value(GlobalErrorCode.NOT_SUPPORT_TYPE.getMessage()))
			.andExpect(jsonPath("$.success").value(false))
			.andDo(print());
	}

	@DisplayName("채용 공고 추천 삭제시 데이터가 존재하지 않을 때 실패 테스트")
	@Test
	void delete_voter_job_posting_not_found_fail() throws Exception {
		//given
		VoterCreateRequest givenRequest = VoterCreateRequest.builder()
			.voterType(VoterType.JOB_POSTING)
			.targetId(1L)
			.build();

		//when
		ResultActions resultActions = mockMvc
			.perform(delete("/api/v1/voter/{targetId}?voterType=job_posting", 999L)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + accessToken1));

		//then
		resultActions
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value(GlobalErrorCode.VOTER_NOT_FOUND.getCode()))
			.andExpect(jsonPath("$.message").value(GlobalErrorCode.VOTER_NOT_FOUND.getMessage()))
			.andExpect(jsonPath("$.success").value(false))
			.andDo(print());
	}
}

package com.backend.domain.recruitment;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.backend.domain.user.entity.SiteUser;
import com.backend.domain.user.repository.UserRepository;
import com.backend.global.security.custom.CustomUserDetails;
import com.backend.standard.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(scripts = {"/sql/init.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(scripts = {"/sql/delete.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RecruitmentUserTest {
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserRepository userRepository;

    @Autowired
    JwtUtil jwtUtil;

    @Value("${jwt.token.access-expiration}")
    long accessExpiration;

    String accessToken1;
    String accessToken2;
    String accessToken3;

    @BeforeAll
    void setUp() {
        SiteUser user1 = userRepository.findByEmail("testEmail1@naver.com").get();
        SiteUser user2 = userRepository.findByEmail("testEmail2@naver.com").get();
        SiteUser user3 = userRepository.findByEmail("testEmail2@naver.com").get();

        CustomUserDetails userDetails1 = new CustomUserDetails(user1);
        CustomUserDetails userDetails2 = new CustomUserDetails(user2);
        CustomUserDetails userDetails3 = new CustomUserDetails(user3);

        accessToken1 = jwtUtil.createAccessToken(userDetails1, accessExpiration);
        accessToken2 = jwtUtil.createAccessToken(userDetails2, accessExpiration);
        accessToken3 = jwtUtil.createAccessToken(userDetails3, accessExpiration);
    }

    @Test
    @DisplayName("모집 신청 테스트")
    void applyForRecruitmentTest() throws Exception {
        Long postId = 3L;

        mockMvc.perform(post("/api/v1/recruitment-user/{postId}", postId)
                        .header("Authorization", "Bearer " + accessToken1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andDo(print());
    }

    @Test
    @DisplayName("모집 신청 취소 테스트")
    void cancelRecruitmentTest() throws Exception {
        Long postId = 1L;

        mockMvc.perform(delete("/api/v1/recruitment-user/{postId}", postId)
                        .header("Authorization", "Bearer " + accessToken2)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andDo(print());
    }

    @Test
    @DisplayName("모집 게시글 페이징 조회 테스트")
    @Order(1)
    void getAcceptedPostsTest() throws Exception {
        Long postId = 1L;
        int pageNum = 0;
        int pageSize = 10;

        mockMvc.perform(get("/api/v1/recruitment-user/accept-posts")
                        .header("Authorization", "Bearer " + accessToken2)
                        .param("pageNum", String.valueOf(pageNum))
                        .param("pageSize", String.valueOf(pageSize)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())  // 여기 수정
                .andExpect(jsonPath("$.data.pageable.pageNumber").value(pageNum))
                .andExpect(jsonPath("$.data.pageable.pageSize").value(pageSize))
                .andDo(print());
    }

    @Test
    @DisplayName("모집 신청 상태 조회 테스트")
    void getRecruitmentStatusTest() throws Exception {

        mockMvc.perform(get("/api/v1/recruitment-user/accept-posts")
                        .header("Authorization", "Bearer " + accessToken2)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].status").value("ACCEPTED"))
                .andDo(print());
    }

    @Test
    @DisplayName("모집 신청이 없는 게시글 조회 테스트")
    void getRecruitmentPostNotFoundTest() throws Exception {
        Long postId = 999L;

        mockMvc.perform(get("/api/v1/recruitment-user/{postId}/status", postId)
                        .header("Authorization", "Bearer " + accessToken2)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());
    }
}
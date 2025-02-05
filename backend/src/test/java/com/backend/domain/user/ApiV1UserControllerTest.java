package com.backend.domain.user;

import com.backend.domain.jobskill.entity.JobSkill;
import com.backend.domain.jobskill.repository.JobSkillRepository;
import com.backend.domain.user.dto.request.JobSkillRequest;
import com.backend.domain.user.dto.request.LoginRequest;
import com.backend.domain.user.dto.request.UserModifyProfileRequest;
import com.backend.domain.user.entity.SiteUser;
import com.backend.domain.user.entity.UserRole;
import com.backend.domain.user.repository.UserRepository;
import com.backend.global.annotation.CustomWithMock;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ApiV1UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JobSkillRepository jobSkillRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;


    private SiteUser testUser;
    private SiteUser otherUser;
    private JobSkill jobSkill1;
    private JobSkill jobSkill2;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        
        testUser = userRepository.save(SiteUser.builder()
                .email("test@test.com")
                .password("password")
                .name("testUser")
                .userRole(UserRole.ROLE_USER.toString())
                .build());

        otherUser = userRepository.save(SiteUser.builder()
                .email("other@test.com")
                .password("password")
                .name("otherUser")
                .userRole(UserRole.ROLE_USER.toString())
                .build());

        jobSkill1 = jobSkillRepository.save(JobSkill.builder()
                .name("직무1")
                .code(1)
                .build());

        jobSkill2 = jobSkillRepository.save(JobSkill.builder()
                .name("직무2")
                .code(2)
                .build());
    }

    @Test
    @DisplayName("프로필 조회 성공")
    @CustomWithMock
    void test1() throws Exception {

        SiteUser siteUser = SiteUser.builder()
                .email("test1@test.com")
                .password(passwordEncoder.encode("password"))
                .name("test1")
                .userRole(UserRole.ROLE_ADMIN.toString())
                .build();
        userRepository.save(siteUser);

        String accessToken = mockMvc.perform(post("/api/v1/adm/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest("test1@test.com", "password"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(header().exists("Authorization"))
                .andExpect(cookie().exists("refreshToken"))
                .andExpect(jsonPath("$.data.email").value("test1@test.com"))
                .andDo(print())
                .andReturn()
                .getResponse()
                .getHeader("Authorization");

        mockMvc.perform(get("/api/v1/users/{id}", siteUser.getId())
                        .header("Authorization", "Bearer " + accessToken)  // Bearer 추가
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.email").value(siteUser.getEmail()))
                .andExpect(jsonPath("$.data.name").value(siteUser.getName()))
                .andDo(print());
    }

    @Test
    @DisplayName("프로필 조회 실패 - 비로그인 사용자")
    void test2() throws Exception {
        mockMvc.perform(get("/api/v1/users/{user_id}", testUser.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(4002))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("프로필 조회 실패 - 다른 사용자의 프로필 접근")
    void test3() throws Exception {
        SiteUser targetUser = SiteUser.builder()
                .email("target@test.com")
                .password(passwordEncoder.encode("password"))
                .name("target")
                .userRole(UserRole.ROLE_USER.toString())
                .build();
        userRepository.save(targetUser);

        SiteUser loginUser = SiteUser.builder()
                .email("test1@test.com")
                .password(passwordEncoder.encode("password"))
                .name("test1")
                .userRole(UserRole.ROLE_ADMIN.toString())
                .build();
        userRepository.save(loginUser);

        String accessToken = mockMvc.perform(post("/api/v1/adm/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest("test1@test.com", "password"))))
                .andReturn()
                .getResponse()
                .getHeader("Authorization");

        mockMvc.perform(get("/api/v1/users/{id}", targetUser.getId())  // 존재하는 다른 사용자의 ID로 접근
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(4003))
                .andDo(print());
    }

    @Test
    @DisplayName("프로필 수정 성공")
    @CustomWithMock
    void test4() throws Exception {
        SiteUser siteUser = SiteUser.builder()
                .email("test1@test.com")
                .password(passwordEncoder.encode("password"))
                .name("test1")
                .userRole(UserRole.ROLE_ADMIN.toString())
                .build();
        userRepository.save(siteUser);

        List<JobSkillRequest> jobSkills = List.of(
                new JobSkillRequest("직무1"),
                new JobSkillRequest("직무2")
        );

        UserModifyProfileRequest request = new UserModifyProfileRequest();
        request.setIntroduction("자기소개수정");
        request.setJob("직업수정");
        request.setJobSkills(jobSkills);

        String accessToken = mockMvc.perform(post("/api/v1/adm/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest("test1@test.com", "password"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(header().exists("Authorization"))
                .andExpect(cookie().exists("refreshToken"))
                .andExpect(jsonPath("$.data.email").value("test1@test.com"))
                .andDo(print())
                .andReturn()
                .getResponse()
                .getHeader("Authorization");

        mockMvc.perform(patch("/api/v1/users/{id}", siteUser.getId())
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.code").value(200))
                .andDo(print());

        // 실제 데이터베이스에 반영되었는지 확인
        SiteUser updatedUser = userRepository.findById(siteUser.getId()).orElseThrow();
        assertThat(updatedUser.getIntroduction()).isEqualTo("자기소개수정");
        assertThat(updatedUser.getJob()).isEqualTo("직업수정");
        assertThat(updatedUser.getJobSkills()).hasSize(2);
        assertThat(updatedUser.getJobSkills().get(0).getName()).isEqualTo("직무1");
        assertThat(updatedUser.getJobSkills().get(1).getName()).isEqualTo("직무2");
    }

    @Test
    @DisplayName("프로필 수정 실패 - 비로그인 사용자")
    void test5() throws Exception {
        SiteUser siteUser = SiteUser.builder()
                .email("test1@test.com")
                .password(passwordEncoder.encode("password"))
                .name("test1")
                .userRole(UserRole.ROLE_ADMIN.toString())
                .build();
        userRepository.save(siteUser);

        List<JobSkillRequest> jobSkills = List.of(
                new JobSkillRequest("직무1"),
                new JobSkillRequest("직무2")
        );

        UserModifyProfileRequest request = new UserModifyProfileRequest();
        request.setIntroduction("자기소개수정");
        request.setJob("직업수정");
        request.setJobSkills(jobSkills);

        mockMvc.perform(patch("/api/v1/users/{id}", siteUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(4002))
                .andDo(print());

        SiteUser unchangedUser = userRepository.findById(siteUser.getId()).orElseThrow();
        assertThat(unchangedUser.getIntroduction()).isNull();
        assertThat(unchangedUser.getJob()).isNull();
        assertThat(unchangedUser.getJobSkills()).isEmpty();
    }

    @Test
    @DisplayName("프로필 수정 실패 - 다른 사용자의 프로필 수정 시도")
    void test6() throws Exception {
        SiteUser targetUser = SiteUser.builder()
                .email("target@test.com")
                .password(passwordEncoder.encode("password"))
                .name("target")
                .userRole(UserRole.ROLE_USER.toString())
                .build();
        userRepository.save(targetUser);

        SiteUser loginUser = SiteUser.builder()
                .email("test1@test.com")
                .password(passwordEncoder.encode("password"))
                .name("test1")
                .userRole(UserRole.ROLE_ADMIN.toString())
                .build();
        userRepository.save(loginUser);

        List<JobSkillRequest> jobSkills = List.of(
                new JobSkillRequest("직무1"),
                new JobSkillRequest("직무2")
        );

        UserModifyProfileRequest request = new UserModifyProfileRequest();
        request.setIntroduction("자기소개수정");
        request.setJob("직업수정");
        request.setJobSkills(jobSkills);

        String accessToken = mockMvc.perform(post("/api/v1/adm/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest("test1@test.com", "password"))))
                .andExpect(status().isOk())
                .andExpect(header().exists("Authorization"))
                .andReturn()
                .getResponse()
                .getHeader("Authorization");

        mockMvc.perform(patch("/api/v1/users/{id}", targetUser.getId())
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())  // 401 -> 403으로 변경
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(4003))  // 4002 -> 4003으로 변경
                .andDo(print());

        SiteUser unchangedUser = userRepository.findById(targetUser.getId()).orElseThrow();
        assertThat(unchangedUser.getIntroduction()).isNull();
        assertThat(unchangedUser.getJob()).isNull();
        assertThat(unchangedUser.getJobSkills()).isEmpty();
    }

}

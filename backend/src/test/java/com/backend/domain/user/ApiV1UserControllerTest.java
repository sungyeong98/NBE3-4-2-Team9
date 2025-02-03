package com.backend.domain.user;

import com.backend.domain.user.entity.SiteUser;
import com.backend.domain.user.entity.UserRole;
import com.backend.domain.user.repository.UserRepository;
import com.backend.global.annotation.CustomWithMock;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public class ApiV1UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    private SiteUser testUser;

    private SiteUser otherUser;

    @BeforeEach
    void setUp() {
        testUser = userRepository.save(SiteUser.builder()
                .name("testUser")
                .email("test@test.com")
                .password("password")
                .userRole(UserRole.ROLE_USER.toString())
                .build());

        otherUser = userRepository.save(SiteUser.builder()
                .name("otherUser")
                .email("other@test.com")
                .password("password")
                .userRole(UserRole.ROLE_USER.toString())
                .build());
    }

    @Test
    @DisplayName("프로필 조회 성공")
    @CustomWithMock
    void test1() throws Exception {
        mockMvc.perform(get("/api/v1/users/{user_id}", testUser.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.name").value(testUser.getName()))
                .andExpect(jsonPath("$.data.email").value(testUser.getEmail()))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("프로필 조회 실패 - 비로그인 사용자")
    void test2() throws Exception {
        mockMvc.perform(get("/api/v1/users/{user_id}", testUser.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(401))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("프로필 조회 실패 - 다른 사용자의 프로필 접근")
    @CustomWithMock
    void test3() throws Exception {
        mockMvc.perform(get("/api/v1/users/{user_id}", otherUser.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(4003))
                .andDo(MockMvcResultHandlers.print());
    }

}

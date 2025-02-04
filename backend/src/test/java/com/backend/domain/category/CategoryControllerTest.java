package com.backend.global.category;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.backend.domain.category.dto.response.CategoryResponse;
import com.backend.domain.category.entity.Category;
import com.backend.domain.category.service.CategoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CategoryService categoryService;

    private Category category;
    private CategoryResponse categoryResponse;

    @BeforeEach
    void setUp() {
        category = new Category(1L, "Tech");
        categoryResponse = new CategoryResponse(1L, "Tech", ZonedDateTime.now(), ZonedDateTime.now());
    }

    /**
     * 통합 테스트: PUT /api/v1/category/{id} (관리자 권한)
     * 관리자 권한을 가진 사용자가 카테고리를 수정하는 요청
     */
    @Test
    @WithMockUser(roles = "ADMIN")  // ADMIN 권한으로 인증된 사용자로 요청
    void updateCategory_WithAdminRole_ShouldReturnUpdatedCategory() throws Exception {
        // given: categoryService.updateCategory()에서 반환될 categoryResponse 설정
        when(categoryService.updateCategory(category)).thenReturn(categoryResponse);

        // when & then: PUT 요청을 보낼 때 CSRF 토큰 추가, 응답 상태가 200(OK) 확인
        mockMvc.perform(put("/api/v1/category/{id}", 1L)
                        .with(csrf())  // CSRF 보호 활성화
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(category)))  // 카테고리 정보가 담긴 JSON
                .andExpect(status().isOk())  // 응답 상태 코드가 200인지 확인
                .andExpect(jsonPath("$.success").value(true))  // 응답의 success가 true인지 확인
                .andExpect(jsonPath("$.data.name").value("Tech"));  // 수정된 카테고리의 name이 "Tech"인지 확인
    }

    /**
     * 통합 테스트: PUT /api/v1/category/{id} (관리자 권한이 아닌 사용자)
     * 관리자가 아닌 사용자가 카테고리 수정 시 403 Forbidden 응답을 받는지 검증
     */
    @Test
    @WithMockUser(roles = "USER")  // USER 권한으로 인증된 사용자로 요청
    void updateCategory_WithUserRole_ShouldReturnForbidden() throws Exception {
        // when & then: PUT 요청 시, 응답 상태가 403(Forbidden)임을 확인
        mockMvc.perform(put("/api/v1/category/{id}", 1L)
                        .with(csrf())  // CSRF 보호 활성화
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(category)))  // 카테고리 정보가 담긴 JSON
                .andExpect(status().isForbidden());  // 권한 부족으로 인해 403 Forbidden이 반환되어야 함
    }

    /**
     * 통합 테스트: PUT /api/v1/category/{id} (ID 불일치)
     * 요청한 카테고리 ID와 URL ID가 일치하지 않으면 400 Bad Request 응답을 받는지 검증
     */
    @Test
    @WithMockUser(roles = "ADMIN")  // ADMIN 권한으로 인증된 사용자로 요청
    void updateCategory_WithMismatchedId_ShouldReturnBadRequest() throws Exception {

        // given: 요청된 ID와 카테고리의 ID가 일치하지 않는 경우
        // 요청한 ID와 일치하지 않도록 설정
        category = Category.builder()
                .id(2L)
                .name("Tech")
                .build();


        // when & then: PUT 요청 시, 응답 상태가 400(Bad Request)임을 확인
        mockMvc.perform(put("/api/v1/category/{id}", 1L)
                        .with(csrf())  // CSRF 보호 활성화
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(category)))  // 카테고리 정보가 담긴 JSON
                .andExpect(status().isBadRequest())  // 요청된 ID와 일치하지 않아서 400 상태 코드가 반환되어야 함
                .andExpect(jsonPath("$.message").value("카테고리 ID가 유효하지 않습니다."));  // 예외 메시지 확인
    }
}

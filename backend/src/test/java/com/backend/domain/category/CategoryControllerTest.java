package com.backend.global.category;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.backend.domain.category.controller.CategoryController;
import com.backend.domain.category.dto.response.CategoryResponse;
import com.backend.domain.category.entity.Category;
import com.backend.domain.category.service.CategoryService;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CategoryController.class)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    private Category category;
    private CategoryResponse categoryResponse;
    private ZonedDateTime now;

    @BeforeEach
    void setUp() {
        now = ZonedDateTime.now();

        // 카테고리 객체 초기화
        category = new Category(1L, "Tech");

        // 카테고리 응답 객체 초기화
        categoryResponse = new CategoryResponse(1L, "Tech", now, now);
    }

    // 관리자 권한을 가진 사용자로 설정하는 메서드
    private void mockAdminUser() {
        User user = new User("admin", "password", Arrays.asList(() -> "ROLE_ADMIN"));
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(user, "password", user.getAuthorities()));
        SecurityContextHolder.setContext(context);
    }

    // 카테고리 전체 조회 테스트
    @Test
    @WithMockUser(roles = "ADMIN")  // 관리자 권한 부여
    void getAllCategory_ShouldReturnCategoryList() throws Exception {
        List<CategoryResponse> categoryList = Arrays.asList(categoryResponse);

        when(categoryService.categoryList()).thenReturn(categoryList);

        mockAdminUser();  // 관리자 권한 부여

        mockMvc.perform(get("/api/v1/category"))
                .andExpect(status().isOk())  // CSRF 비활성화로 인해 CSRF 토큰 필요 없음
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].name").value("Tech"));
    }


    // 카테고리 추가 테스트
    @Test
    @WithMockUser(roles = "ADMIN")  // 관리자 권한 부여
    void createCategory_ShouldReturnCreatedCategory() throws Exception {
        when(categoryService.createCategory(category)).thenReturn(categoryResponse);

        mockAdminUser();  // 관리자 권한 부여

        mockMvc.perform(post("/api/v1/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":1, \"name\":\"Tech\"}")
                        .with(csrf()))
                .andExpect(status().isCreated())  // CSRF 비활성화로 인해 CSRF 토큰 필요 없음
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("Tech"));
    }

    // 카테고리 수정 테스트 (ID 일치)
    @Test
    @WithMockUser(roles = "ADMIN")  // 관리자 권한 부여
    void updateCategory_ShouldReturnUpdatedCategory() throws Exception {
        when(categoryService.updateCategory(category)).thenReturn(categoryResponse);

        mockAdminUser();  // 관리자 권한 부여

        mockMvc.perform(put("/api/v1/category/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":1, \"name\":\"Tech\"}")
                        .with(csrf()))
                .andExpect(status().isOk())  // CSRF 비활성화로 인해 CSRF 토큰 필요 없음
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("Tech"));
    }


    // 카테고리 수정 테스트 (ID 불일치)
    @Test
    @WithMockUser(roles = "ADMIN")  // 관리자 권한 부여
    void updateCategory_ShouldThrowInvalidCategoryIdException() throws Exception {
        // 잘못된 ID를 가진 카테고리 객체
        Category category = Category.builder().id(2L).name("Tech").build();

        mockAdminUser();  // 관리자 권한 부여

        mockMvc.perform(put("/api/v1/category/{id}", 1L)  // 1L ID로 요청하지만, 내용은 ID가 2인 카테고리
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":2, \"name\":\"Tech\"}")
                        .with(csrf()))  // CSRF 토큰을 추가
                .andExpect(status().isBadRequest())  // ID 불일치로 인해 Bad Request 발생
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("카테고리 ID가 일치하지 않습니다."));  // 예외 메시지 검증
    }
}

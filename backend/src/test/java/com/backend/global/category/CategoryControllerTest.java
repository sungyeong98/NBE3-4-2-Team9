package com.backend.global.category;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.backend.domain.category.controller.CategoryController;
import com.backend.domain.category.dto.response.CategoryResponse;
import com.backend.domain.category.entity.Category;
import com.backend.domain.category.service.CategoryService;
import com.backend.global.redis.repository.RedisRepository;
import com.backend.global.security.handler.OAuth2LoginFailureHandler;
import com.backend.global.security.handler.OAuth2LoginSuccessHandler;
import com.backend.global.security.oauth.CustomOAuth2UserService;
import com.backend.standard.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.ZonedDateTime;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CategoryController.class)
@ActiveProfiles("test")
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    // CategoryController가 의존하는 서비스 빈을 Mock 객체로 주입
    @MockBean
    private CategoryService categoryService;

    // Jwt 관련 빈들은 SecurtiyConfig에 의해 사용, 여기선 Mock 객체로 주입
    @MockBean
    private JwtUtil jwtUtil;
    @MockBean
    private RedisRepository redisRepository;
    @MockBean
    private CustomOAuth2UserService customOAuth2UserService;
    @MockBean
    private OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    @MockBean
    private OAuth2LoginFailureHandler oAuth2LoginFailureHandler;

    private CategoryController categoryController;
    private Category category;
    private CategoryResponse categoryResponse;

    @BeforeEach
    void setUp() {
        ZonedDateTime now = ZonedDateTime.now();

        // 테스트용 Category 객체 생성 (id = null, name = "Tech")
        category = new Category(null, "Tech");
        ReflectionTestUtils.setField(category, "createdAt", now);
        ReflectionTestUtils.setField(category, "modifiedAt", now);

        // CategoryResponse 빌더로 응답 객체 생성
        categoryResponse = CategoryResponse.builder()
                .id(1L)
                .name("Tech")
                .createdAt(now)
                .modifiedAt(now)
                .build();
    }

    /**
     * GET /api/v1/category 요청 테스트
     *
     * 이 테스트는 인증된 사용자(@WithMockUser가 추가된 경우)로 GET 요청을 보내,
     * 카테고리 목록을 정상적으로 조회하는지 확인합니다.
     *
     * - categoryService.categoryList()를 목 객체에서 미리 정의한 응답(categoryResponse 리스트)을 반환하도록 설정합니다.
     * - 응답 JSON의 success 필드가 true인지, 그리고 첫 번째 데이터의 name 필드가 "Tech"인지 확인합니다.
     */
    @Test
    @WithMockUser
    void getAllCategory_ShouldReturnCategoryList() throws Exception {

        // given: 서비스의 categoryList() 메서드 호출 시, 미리 정의된 categoryResponse 리스트 반환
        when(categoryService.categoryList()).thenReturn(
                Arrays.asList(categoryResponse)
        );

        // when & then: GET 요청을 보내고, 응답 상태가 200, JSON 응답의 특정 필드 값 검증
        mockMvc.perform(get("/api/v1/category"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].name").value("Tech"));
    }

    /**
     * POST /api/v1/category 요청 테스트 (관리자 권한)
     *
     * 이 테스트는 ADMIN 권한을 가진 사용자가 POST 요청을 보내 새로운 카테고리를 생성하는 경우,
     * 정상적으로 생성되었는지(201 상태 코드, success: true, 그리고 생성된 카테고리의 name 필드가 "Tech")를 검증합니다.
     *
     * - CSRF 보호를 위해 요청에 CSRF 토큰을 함께 전송합니다.
     */
    @Test
    @WithMockUser(roles = "ADMIN")  // ADMIN 권한으로 인증된 사용자로 요청
    void createCategory_WithAdminRole_ShouldReturnCreated() throws Exception {

        // given: 서비스의 createCategory() 메서드 호출 시, 미리 정의된 categoryResponse 반환
        when(categoryService.createCategory(any(Category.class))).thenReturn(categoryResponse);

        // when & then: POST 요청을 보낼 때 CSRF 토큰 추가, 응답 상태가 201(Created)임을 확인하고, JSON 응답의 success와 data.name 필드 검증
        mockMvc.perform(post("/api/v1/category")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(category)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("Tech"));
    }

    /**
     * POST /api/v1/category 요청 테스트 (일반 사용자 권한)
     *
     * 이 테스트는 USER 권한을 가진 사용자가 POST 요청을 보내 새로운 카테고리를 생성하려 할 때,
     * 권한 부족으로 인해 요청이 거부되어 403 Forbidden 상태 코드가 반환되는지 검증합니다.
     *
     * - USER 권한으로 인증된 경우에는 ADMIN 전용 기능인 카테고리 생성에 접근할 수 없어야 합니다.
     */
    @Test
    @WithMockUser(roles = "USER")  // USER 권한으로 인증된 사용자로 요청
    void createCategory_WithUserRole_ShouldReturnForbidden() throws Exception {

        // when & then: POST 요청 시 CSRF 토큰 추가, 응답 상태가 403(Forbidden)임을 확인
        mockMvc.perform(post("/api/v1/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(category)))
                .andExpect(status().isForbidden());
    }
}
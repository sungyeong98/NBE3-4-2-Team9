package com.backend.domain.category;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.backend.domain.category.dto.request.CategoryRequest;
import com.backend.domain.category.dto.response.CategoryResponse;
import com.backend.domain.category.entity.Category;
import com.backend.domain.category.repository.CategoryRepository;
import com.backend.domain.category.service.CategoryService;
import com.backend.domain.user.entity.SiteUser;
import com.backend.domain.user.repository.UserRepository;
import com.backend.global.exception.GlobalException;
import com.backend.global.security.custom.CustomUserDetails;
import com.backend.standard.util.JwtUtil;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
@TestPropertySource(properties = {
        "jwt.token.access-expiration=3600",
        "jwt.token.refresh-expiration=7200",
        "jwt.secret=testsecretkeytestsecretkeytestsecretkeytestsecretkey",
        "spring.main.allow-bean-definition-overriding=true"
})
class CategoryServiceTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private JwtUtil jwtUtil;

    private CustomUserDetails adminDetails;
    private CustomUserDetails userDetails;

    private Category savedTech;
    private Category savedScience;

    @BeforeEach
    void setUp() {
        categoryRepository.deleteAll();

        // 사용자 및 카테고리 초기화
        SiteUser adminUser = userRepository.save(SiteUser.builder()
                .email("admin@admin.com")
                .password("admin")
                .name("admin")
                .userRole("ADMIN")
                .build());
        SiteUser normalUser = userRepository.save(SiteUser.builder()
                .email("user@user.com")
                .password("user")
                .name("user")
                .userRole("USER")
                .build());

        adminDetails = new CustomUserDetails(adminUser);
        userDetails = new CustomUserDetails(normalUser);

        savedTech = categoryRepository.save(new Category(null, "Tech"));
        savedScience = categoryRepository.save(new Category(null, "Science"));
    }

    private void setSecurityContext(CustomUserDetails userDetails) {
        List<GrantedAuthority> authorities = new ArrayList<>(userDetails.getAuthorities());

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, authorities);

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    void categoryList_ShouldReturnAllCategories() {
        setSecurityContext(adminDetails);  // 관리자 사용자로 인증

        List<CategoryResponse> result = categoryService.categoryList();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Tech");
        assertThat(result.get(1).getName()).isEqualTo("Science");
    }

    @Test
    void createCategory_WithAdminRole_ShouldCreateCategory() {
        setSecurityContext(adminDetails);  // 관리자 권한으로 인증

        CategoryRequest categoryRequest = new CategoryRequest("Math", null, null);  // CategoryRequest 사용
        CategoryResponse result = categoryService.createCategory(categoryRequest);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Math");
    }

    @Test
    void createCategory_WithUserRole_ShouldThrowException() {
        setSecurityContext(userDetails);  // 일반 사용자로 인증

        CategoryRequest categoryRequest = new CategoryRequest("Math", null, null);  // CategoryRequest 사용

        GlobalException exception = assertThrows(GlobalException.class,
                () -> categoryService.createCategory(categoryRequest));
        assertThat(exception.getMessage()).contains("접근 권한이 없는 유저입니다.");
    }

    @Test
    void testUpdateCategory_withAdminUser() {
        setSecurityContext(adminDetails);  // 관리자 사용자로 인증

        CategoryRequest categoryRequest = new CategoryRequest("Updated Tech", null, null);  // CategoryRequest 사용
        CategoryResponse result = categoryService.updateCategory(savedTech.getId(), categoryRequest);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Updated Tech");
    }

    @Test
    void testUpdateCategory_withUnauthorizedUser() {
        setSecurityContext(userDetails);  // 일반 사용자로 인증

        CategoryRequest categoryRequest = new CategoryRequest("Updated Tech", null, null);  // CategoryRequest 사용
        GlobalException exception = assertThrows(GlobalException.class,
                () -> categoryService.updateCategory(savedTech.getId(), categoryRequest));
        assertThat(exception.getMessage()).contains("접근 권한이 없는 유저입니다.");
    }

    @Test
    void testUpdateCategoryWithInvalidId() {
        setSecurityContext(adminDetails);  // 관리자로 인증

        CategoryRequest categoryRequest = new CategoryRequest("Invalid Category", null, null);  // CategoryRequest 사용
        GlobalException exception = assertThrows(GlobalException.class,
                () -> categoryService.updateCategory(999L, categoryRequest));
        assertThat(exception.getMessage()).contains("카테고리가 존재하지 않습니다.");
    }

    @Test
    void testCategoryNameCheckWithDuplicate() {
        // 이미 setUp()에서 "Tech" 카테고리가 저장되어 있음
        // duplicate인 "Tech"로 등록 시 예외가 발생해야 함.
        assertThrows(GlobalException.class, () -> {
            categoryService.categoryNameCheck(null, "Tech", categoryRepository);
        });
    }
}
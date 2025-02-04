package com.backend.domain.category;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@Import(CategoryServiceTest.TestSecurityConfig.class)
@TestPropertySource(properties = {
        "jwt.token.access-expiration=3600",
        "jwt.token.refresh-expiration=7200",
        "jwt.secret=testsecretkeytestsecretkeytestsecretkeytestsecretkey",
        "spring.main.allow-bean-definition-overriding=true"
})
class CategoryServiceTest {

    @TestConfiguration
    static class TestSecurityConfig {
        @Bean
        public JwtUtil jwtUtil() {
            return new JwtUtil("testsecretkeytestsecretkeytestsecretkeytestsecretkey");
        }

        @Bean
        public ClientRegistrationRepository clientRegistrationRepository() {
            ClientRegistration dummyRegistration = ClientRegistration.withRegistrationId("dummy")
                    .clientId("dummy-client-id")
                    .clientSecret("dummy-client-secret")
                    .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                    .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                    .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
                    .scope("read")
                    .authorizationUri("https://example.com/oauth2/authorize")
                    .tokenUri("https://example.com/oauth2/token")
                    .userInfoUri("https://example.com/oauth2/userinfo")
                    .userNameAttributeName("id")
                    .clientName("dummy")
                    .build();
            return new InMemoryClientRegistrationRepository(dummyRegistration);
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            http.csrf(csrf -> csrf.disable())
                    .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
            return http.build();
        }
    }

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

    // 저장된 Category들을 보관할 필드 (ID를 검증하기 위해)
    private Category savedTech;
    private Category savedScience;

    @BeforeEach
    void setUp() {
        // 관리자 및 일반 사용자 엔티티를 DB에 저장
        // **관리자 역할을 "ADMIN"으로 설정 (내부에서 비교하는 값에 맞게)**
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

        // 카테고리 생성 시, ID는 자동 생성되도록 null로 처리하고 반환값을 보관
        savedTech = categoryRepository.save(new Category(null, "Tech"));
        savedScience = categoryRepository.save(new Category(null, "Science"));
    }

    private void setSecurityContext(CustomUserDetails userDetails) {
        List<GrantedAuthority> authorities = new ArrayList<>(userDetails.getAuthorities());

        // 인증 객체 생성 (권한 포함)
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, authorities);

        // SecurityContext에 인증 정보 설정
        SecurityContextHolder.getContext().setAuthentication(authentication);

        System.out.println("Admin Authorities: " + adminDetails.getAuthorities());

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

        Category newCategory = new Category(null, "Math");
        CategoryResponse result = categoryService.createCategory(newCategory);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Math");
    }

    @Test
    void createCategory_WithUserRole_ShouldThrowException() {
        setSecurityContext(userDetails);  // 일반 사용자로 인증

        Category newCategory = new Category(null, "Math");

        GlobalException exception = assertThrows(GlobalException.class,
                () -> categoryService.createCategory(newCategory));
        assertThat(exception.getMessage()).contains("접근 권한이 없는 유저입니다.");
    }

    @Test
    void testUpdateCategory_withAdminUser() {
        setSecurityContext(adminDetails);  // 관리자 사용자로 인증

        // savedTech를 수정한 후, 새로운 인스턴스에서 업데이트를 처리
        Category updatedCategory = new Category(savedTech.getId(), "Updated Tech");
        CategoryResponse result = categoryService.updateCategory(updatedCategory);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Updated Tech");
    }

    @Test
    void testUpdateCategory_withUnauthorizedUser() {
        setSecurityContext(userDetails);  // 일반 사용자로 인증

        // savedTech에 저장된 카테고리 사용
        GlobalException exception = assertThrows(GlobalException.class,
                () -> categoryService.updateCategory(savedTech));
        assertThat(exception.getMessage()).contains("접근 권한이 없는 유저입니다.");
    }

    @Test
    void testUpdateCategoryWithInvalidId() {
        setSecurityContext(adminDetails);  // 관리자로 인증

        Category invalidCategory = new Category(999L, "Invalid Category");

        GlobalException exception = assertThrows(GlobalException.class,
                () -> categoryService.updateCategory(invalidCategory));
        // TODO : NOT_FOUND 예외 설정
        assertThat(exception.getMessage()).contains("서버 내부 오류가 발생하였습니다.");
    }

    @Test
    void testCategoryNameCheck() {
        setSecurityContext(adminDetails);  // 관리자로 인증

        // 이름이 빈 값인 카테고리 테스트
        Category invalidCategory = new Category(savedTech.getId(), "");

        GlobalException exception = assertThrows(GlobalException.class,
                () -> categoryService.updateCategory(invalidCategory));
        assertThat(exception.getMessage()).contains("카테고리 이름이 유효하지 않습니다.");
    }
}

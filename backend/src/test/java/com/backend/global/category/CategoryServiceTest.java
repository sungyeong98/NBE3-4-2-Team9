package com.backend.global.category;

import static com.backend.domain.category.converter.CategoryConverter.mappingCategory;
import static com.backend.domain.category.converter.CategoryConverter.mappingCategoryList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

import com.backend.domain.category.converter.CategoryConverter;
import com.backend.domain.category.dto.response.CategoryResponse;
import com.backend.domain.category.entity.Category;
import com.backend.domain.category.repository.CategoryRepository;
import com.backend.domain.category.service.CategoryService;
import com.backend.domain.user.entity.SiteUser;
import com.backend.domain.user.entity.UserRole;
import com.backend.global.exception.GlobalException;
import com.backend.global.security.custom.CustomUserDetails;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    // Repository를 Mock 객체로 주입
    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryConverter categoryConverter;

    // Security 관련 Mock 객체들
    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;

    // 테스트 대상 객체(CategoryService) 생성, 목 객체들이 주입됨
    @InjectMocks
    private CategoryService categoryService;
    private Category category;
    private CustomUserDetails adminDetails;
    private CustomUserDetails userDetails;
    private ZonedDateTime now;

    @BeforeEach
    void setUp() {

        // 테스트 실행 시점의 현재 시간을 저장
        now = ZonedDateTime.now();

        // Category 객체 생성 (id: 1, name: "Tech")
        // BaseEntity 상속으로 인해 createdAt, modifiedAt 필드가 있지만, 여기서는 Reflection을 통해 강제로 값을 주입
        category = new Category(1L, "Tech");
        ReflectionTestUtils.setField(category, "createdAt", now);
        ReflectionTestUtils.setField(category, "modifiedAt", now);

        // Admin 사용자 생성
        SiteUser adminUser = SiteUser.builder()
                .id(1L)
                .email("admin@admin.com")
                .password("admin")
                .name("admin")
                .userRole(UserRole.ROLE_ADMIN.toString())
                .build();

        // 일반 사용자 생성
        SiteUser normalUser = SiteUser.builder()
                .id(2L)
                .email("user@user.com")
                .password("user")
                .name("user")
                .userRole(UserRole.ROLE_USER.toString())
                .build();

        // CustomUserDetails 객체로 변환
        adminDetails = new CustomUserDetails(adminUser);
        userDetails = new CustomUserDetails(normalUser);
    }

    /**
     * 테스트: categoryList_ShouldReturnAllCategories
     *
     * 목적:
     * - CategoryService의 categoryList() 메서드가 CategoryRepository에서 조회한 모든 카테고리를
     *   CategoryResponse 리스트로 변환하여 반환하는지 검증.
     *
     * 동작:
     * - 두 개의 Category 객체(예: "Tech", "Science")를 Repository가 반환하도록 설정.
     * - 반환된 리스트의 사이즈와 각 요소의 name 값이 예상과 일치하는지 확인.
     */
    @Test
    void categoryList_ShouldReturnAllCategories() {

        // given: "Science" 카테고리 객체 생성 및 createdAt, modifiedAt 값 설정
        Category category2 = new Category(2L, "Science");
        ReflectionTestUtils.setField(category2, "createdAt", now);
        ReflectionTestUtils.setField(category2, "modifiedAt", now);

        // 두 개의 Category 객체를 포함하는 리스트를 목 객체에서 반환하도록 설정
        List<Category> categories = Arrays.asList(category, category2);
        when(categoryRepository.findAll()).thenReturn(categories);

        // categoryConverter의 동작을 정의 (Mock 객체는 기본적으로 동작 X)
        when(mappingCategoryList(anyList()))
                .thenReturn(Arrays.asList(
                        new CategoryResponse(1L, "Tech", now, now),
                        new CategoryResponse(2L, "Science", now, now)
                ));

        // when: service의 categoryList() 호출
        List<CategoryResponse> result = categoryService.categoryList();

        // then: 반환된 리스트의 사이즈와 각 CategoryResponse 객체의 값 검증
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Tech");
        assertThat(result.get(1).getName()).isEqualTo("Science");
    }

    /**
     * 테스트: createCategory_WithAdminRole_ShouldCreateCategory
     *
     * 목적:
     * - ADMIN 권한을 가진 사용자가 카테고리 생성 요청을 했을 때, CategoryService가 정상적으로
     *   카테고리를 저장하고 생성된 CategoryResponse를 반환하는지 검증.
     *
     * 동작:
     * - SecurityContextHolder에 목(SecurityContext) 객체를 설정하고, 인증 정보로 adminDetails를 반환하도록 설정.
     * - Repository의 save() 메서드를 호출하면 미리 설정한 Category 객체를 반환하도록 설정.
     * - 반환된 CategoryResponse의 값이 예상대로 생성되었는지 확인.
     */
    @Test
    void createCategory_WithAdminRole_ShouldCreateCategory() {

        // given: SecurityContextHolder에 목(SecurityContext) 객체 설정
        SecurityContextHolder.setContext(securityContext);

        // 인증(Authentication) 객체가 adminDetails를 반환하도록 설정
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(adminDetails);

        // Repository의 save() 호출 시, 미리 설정한 category 객체 반환
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        // categoryConverter의 동작을 정의 (Mock 객체는 기본적으로 동작 X)
        when(mappingCategory(any()))
                .thenReturn(
                        new CategoryResponse(1L, "Tech", now, now)
                );

        // when: CategoryService의 createCategory() 호출
        CategoryResponse result = categoryService.createCategory(category);

        // then: 반환된 CategoryResponse의 값 검증 (id, name 등이 올바르게 설정되었는지)
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Tech");
    }

    /**
     * 테스트: createCategory_WithUserRole_ShouldThrowException
     *
     * 목적:
     * - 일반 사용자(ROLE_USER)가 카테고리 생성 요청을 했을 때, 권한 부족으로 인해 GlobalException이 발생하는지 검증.
     *
     * 동작:
     * - SecurityContextHolder에 목(SecurityContext) 객체 설정 후, 인증 정보로 userDetails를 반환하도록 설정.
     * - CategoryService의 createCategory() 호출 시, 예외가 발생하는지 확인.
     */
    @Test
    void createCategory_WithUserRole_ShouldThrowException() {

        // given: SecurityContextHolder에 목(SecurityContext) 객체 설정
        SecurityContextHolder.setContext(securityContext);

        // 인증(Authentication) 객체가 userDetails를 반환하도록 설정 (일반 사용자)
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        // when & then: createCategory() 호출 시 GlobalException이 발생하는지 검증
        assertThatThrownBy(() -> categoryService.createCategory(category))
                .isInstanceOf(GlobalException.class);
    }
}
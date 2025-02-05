package com.backend.domain.category.converter;

import com.backend.domain.category.dto.response.CategoryResponse;
import com.backend.domain.category.entity.Category;
import com.backend.domain.category.repository.CategoryRepository;
import com.backend.domain.user.entity.UserRole;
import com.backend.global.exception.GlobalErrorCode;
import com.backend.global.exception.GlobalException;
import com.backend.global.security.custom.CustomUserDetails;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CategoryConverter {

    // 현재 인증된 사용자의 역할(UserRole)을 반환하는 메서드
    public static UserRole userRoleFormString() {

        // 인증된 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 인증 정보가 없거나, 사용자가 인증되지 않은 경우 예외 발생
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new GlobalException(GlobalErrorCode.UNAUTHORIZATION_USER);
        }

        // Principal 객체 가져오기
        Object principal = authentication.getPrincipal();

        // Principal이 CustomUserDetails 타입이 아닌 경우 예외 발생
        if (!(principal instanceof CustomUserDetails userDetails)) {
            throw new GlobalException(GlobalErrorCode.UNAUTHORIZATION_USER);
        }

        // Enum으로 반환
        UserRole userRole = UserRole.fromString(userDetails.getRole());

        // 관리자 권한 체크
        if (!userRole.isAdmin()) {
            throw new GlobalException(GlobalErrorCode.UNAUTHORIZATION_USER);
        }

        return userRole;
    }

    // 유효성 검사 및 중복 검사
    public static void categoryNameCheck(
        Category category, CategoryRepository categoryRepository) {

        // 유효성 검사
        if (category.getName() == null || category.getName().isEmpty()) {
            throw new GlobalException(GlobalErrorCode.INVALID_CATEGORY_NAME);
        }

        // 중복이 있는지 확인
        if (categoryRepository.existsByName(category.getName())) {
            throw new GlobalException(GlobalErrorCode.DUPLICATED_CATEGORY_NAME);
        }
    }

    // 카테고리 리스트 매핑
    public static List<CategoryResponse> mappingCategoryList(List<Category> categoryList) {
        return categoryList.stream()
                .map(category -> CategoryResponse.builder()
                        .id(category.getId())
                        .name(category.getName())
                        .createdAt(category.getCreatedAt())
                        .modifiedAt(category.getModifiedAt())
                        .build())
                .collect(Collectors.toList());
    }

    // 카테고리 매핑 (단일 객체)
    public static CategoryResponse mappingCategory(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .createdAt(category.getCreatedAt())
                .modifiedAt(category.getModifiedAt())
                .build();
    }
}

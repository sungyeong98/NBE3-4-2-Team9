package com.backend.domain.category.service;

import static com.backend.domain.category.converter.CategoryConverter.changeEntity;
import static com.backend.domain.category.converter.CategoryConverter.mappingCategory;
import static com.backend.domain.category.converter.CategoryConverter.mappingCategoryList;

import com.backend.domain.category.dto.request.CategoryRequest;
import com.backend.domain.category.dto.response.CategoryResponse;
import com.backend.domain.category.entity.Category;
import com.backend.domain.category.repository.CategoryRepository;
import com.backend.domain.user.entity.UserRole;
import com.backend.global.exception.GlobalErrorCode;
import com.backend.global.exception.GlobalException;
import com.backend.global.security.custom.CustomUserDetails;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    // 카테고리 전체 조회
    public List<CategoryResponse> categoryList() {
        List<Category> categoryList = categoryRepository.findAll();
        return mappingCategoryList(categoryList);
    }

    // 카테고리 추가 (관리자만 등록 가능)
    public CategoryResponse createCategory(@Valid CategoryRequest categoryRequest) {

        // 권한 검사
//        AdminCheck();

        // 중복 검사
        categoryNameCheck(null, categoryRequest.getName(), categoryRepository);

        // DTO -> Entity로 변환
        Category category = changeEntity(categoryRequest);

        // DB에 저장
        Category saveCategory = categoryRepository.save(category);

        // 응답 객체로 변환 후 반환
        return mappingCategory(saveCategory);
    }

    @Transactional
    // 카테고리 수정 (관리자만 가능)
    public CategoryResponse updateCategory(Long id, @Valid CategoryRequest categoryRequest) {

        // 권한 검사
        AdminCheck();

        // 중복 검사
        categoryNameCheck(id, categoryRequest.getName(), categoryRepository);

        // 관리자일 경우 기존 카테고리 id로 조회, 없으면 NOT_FOUND 예외 처리
        Category findCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new GlobalException(GlobalErrorCode.CATEGORY_NOT_FOUND));

        // 더티 체킹으로 값 변경
        findCategory.updateName(categoryRequest.getName());

        // 응답 객체로 변환 후 반환
        return mappingCategory(findCategory);
    }

    // 현재 인증된 사용자의 역할(UserRole)을 반환하는 메서드
    private void AdminCheck() {

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
    }

    public void categoryNameCheck(Long id, String name, CategoryRepository categoryRepository) {
        if (id != null) {
            if (categoryRepository.existsByNameAndIdNot(name, id)) {
                throw new GlobalException(GlobalErrorCode.DUPLICATED_CATEGORY_NAME);
            }
        } else {
            if (categoryRepository.existsByName(name)) {
                throw new GlobalException(GlobalErrorCode.DUPLICATED_CATEGORY_NAME);
            }
        }
    }
}
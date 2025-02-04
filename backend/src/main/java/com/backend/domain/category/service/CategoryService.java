package com.backend.domain.category.service;

import static com.backend.domain.category.converter.CategoryConverter.categoryNameCheck;
import static com.backend.domain.category.converter.CategoryConverter.mappingCategory;
import static com.backend.domain.category.converter.CategoryConverter.mappingCategoryList;
import static com.backend.domain.category.converter.CategoryConverter.userRoleFormString;

import com.backend.domain.category.dto.response.CategoryResponse;
import com.backend.domain.category.entity.Category;
import com.backend.domain.category.repository.CategoryRepository;
import com.backend.domain.user.entity.UserRole;
import com.backend.global.exception.GlobalErrorCode;
import com.backend.global.exception.GlobalException;
import java.util.List;
import lombok.RequiredArgsConstructor;
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
    public CategoryResponse createCategory(Category category) {

        // 인증된 사용자의 역할을 확인 후, 관리자 권한이 없으면 예외 발생
        UserRole userRole = userRoleFormString();

        // 유효성 검사 및 중복 검사
        categoryNameCheck(category, categoryRepository);

        // 관리자일 경우 카테고리 등록 로직 실행
        Category saveCategory = categoryRepository.save(category);

        // 응답 객체로 변환 후 반환
        return mappingCategory(saveCategory);
    }

    @Transactional
    // 카테고리 수정 (관리자만 가능)
    public CategoryResponse updateCategory(Category category) {

        // 인증된 사용자의 역할을 확인 후, 관리자 권한이 없으면 예외 발생
        UserRole userRole = userRoleFormString();

        // 관리자일 경우 기존 카테고리 조회
        Category findCategory = categoryRepository.findById(category.getId())
                // TODO : 카테고리 NOT_FOUND 예외 설정
                .orElseThrow(() -> new GlobalException(GlobalErrorCode.INTERNAL_SERVER_ERROR));

        // 유효성 검사 및 중복 검사
        categoryNameCheck(category, categoryRepository);

        // 더티 체킹
        findCategory.updateName(category.getName());

        // 카테고리 수정된 상태로 저장
        categoryRepository.save(findCategory);

        // 응답 객체로 변환 후 반환
        return mappingCategory(findCategory);
    }
}
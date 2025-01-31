package com.backend.domain.category.service;

import com.backend.domain.category.dto.response.CategoryResponse;
import com.backend.domain.category.entity.Category;
import com.backend.domain.category.repository.CategoryRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    // 카테고리 전체 조회
    public List<CategoryResponse> categoryList() {
        List<Category> categoryList = categoryRepository.findAll();
        return mappingCategoryList(categoryList);
    }

    // 카테고리 매핑
    private List<CategoryResponse> mappingCategoryList(List<Category> categoryList) {
        return categoryList.stream()
                .map(category -> CategoryResponse.builder()
                        .id(category.getId())
                        .name(category.getName())
                        .createdAt(category.getCreatedAt().toLocalDateTime())
                        .modifiedAt(category.getModifiedAt().toLocalDateTime())
                        .build())
                .collect(Collectors.toList());
    }

    // 카테고리 매핑 (단일 객체)
    private CategoryResponse mappingCategory(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .createdAt(category.getCreatedAt().toLocalDateTime())
                .modifiedAt(category.getModifiedAt().toLocalDateTime())
                .build();
    }
}
package com.backend.domain.category.converter;

import com.backend.domain.category.dto.response.CategoryResponse;
import com.backend.domain.category.entity.Category;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class CategoryConverter {

    // 카테고리 리스트 매핑
    public List<CategoryResponse> mappingCategoryList(List<Category> categoryList) {
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
    public CategoryResponse mappingCategory(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .createdAt(category.getCreatedAt())
                .modifiedAt(category.getModifiedAt())
                .build();
    }
}

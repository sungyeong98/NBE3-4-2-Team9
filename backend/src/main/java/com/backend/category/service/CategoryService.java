package com.backend.category.service;

import com.backend.category.dto.response.CategoryResponse;
import com.backend.category.entity.Category;
import com.backend.category.repository.CategoryRepository;
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
        return mappingCategory(categoryList);
    }

    // 카테고리 매핑
    public List<CategoryResponse> mappingCategory(List<Category> categoryList) {
        return categoryList.stream()
                .map(category -> CategoryResponse.builder()
                        .id(category.getId())
                        .name(category.getName())
                        .createdAt(category.getCreatedAt().toLocalDateTime())
                        .modifiedAt(category.getModifiedAt().toLocalDateTime())
                        .build())
                .collect(Collectors.toList());
    }
}
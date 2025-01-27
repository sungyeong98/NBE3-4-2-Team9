package com.backend.Category.service;

import com.backend.Category.dto.CategoryDto;
import com.backend.Category.entity.Category;
import com.backend.Category.repository.CategoryRepository;
import com.backend.global.response.GenericResponse;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    // 카테고리 전체 조회
    public List<CategoryDto> categoryList() {
        List<Category> categoryList = categoryRepository.findAll();
        return mappingCategory(categoryList);
    }

    // 카테고리 매핑
    public List<CategoryDto> mappingCategory(List<Category> categoryList) {
        return categoryList.stream()
                .map(category -> CategoryDto.builder()
                        .id(category.getId())
                        .name(category.getName())
                        .createdAt(category.getCreatedAt().toLocalDateTime())
                        .modifiedAt(category.getModifiedAt().toLocalDateTime())
                        .build())
                .collect(Collectors.toList());
    }
}
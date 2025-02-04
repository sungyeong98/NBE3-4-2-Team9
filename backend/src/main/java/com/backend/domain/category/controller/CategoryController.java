package com.backend.domain.category.controller;

import com.backend.domain.category.dto.response.CategoryResponse;
import com.backend.domain.category.entity.Category;
import com.backend.domain.category.service.CategoryService;
import com.backend.global.response.GenericResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    // 카테고리 전체 조회
    @GetMapping
    public GenericResponse<List<CategoryResponse>> getAllCategory() {
        List<CategoryResponse> categorieList = categoryService.categoryList();
        return GenericResponse.of(true, HttpStatus.OK.value(), categorieList);
    }

    // 카테고리 추가 (관리자만 가능)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GenericResponse<CategoryResponse> createCategory(@RequestBody Category category) {
        CategoryResponse categoryResponse = categoryService.createCategory(category);
        return GenericResponse.of(true, HttpStatus.CREATED.value(), categoryResponse);
    }

    // 카테고리 수정 (관리자만 가능)
    @PutMapping("/{id}")
    public GenericResponse<CategoryResponse> updateCategory(@RequestBody Category category) {
        CategoryResponse categoryResponse = categoryService.updateCategory(category);
        return GenericResponse.of(true, HttpStatus.OK.value(), categoryResponse);
    }
}

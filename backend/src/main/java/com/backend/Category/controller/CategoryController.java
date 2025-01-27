package com.backend.Category.controller;

import com.backend.Category.entity.Category;
import com.backend.Category.repository.CategoryRepository;
import com.backend.Category.service.CategoryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final CategoryRepository categoryRepository;

    // 카테고리 전체 조회
    @GetMapping
    public ResponseEntity<List<Category>> getAllCategory() {
        List<Category> categorieList = categoryService.categoryList();
        return ResponseEntity.ok(categorieList);
    }

    // 카테고리 등록
    @PostMapping("/create")
    public ResponseEntity<Category> createCategory(@RequestBody Category category) {
        Category newCategory = categoryRepository.save(category);
        return ResponseEntity.ok(newCategory);
    }
}

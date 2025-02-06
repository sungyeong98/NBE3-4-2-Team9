package com.backend.domain.category.controller;

import com.backend.domain.category.converter.CategoryConverter;
import com.backend.domain.category.dto.request.CategoryRequest;
import com.backend.domain.category.dto.response.CategoryResponse;
import com.backend.domain.category.service.CategoryService;
import com.backend.global.exception.GlobalErrorCode;
import com.backend.global.exception.GlobalException;
import com.backend.global.response.GenericResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
    @ResponseStatus(HttpStatus.CREATED)     // 200번이 나와서 임시로 CREATED 설정
    public GenericResponse<CategoryResponse> createCategory(@RequestBody CategoryRequest categoryRequest) {
        CategoryResponse categoryResponse = categoryService.createCategory(
                CategoryConverter.changeEntity(categoryRequest)
        );
        return GenericResponse.of(true, HttpStatus.CREATED.value(), categoryResponse);
    }

    // 카테고리 수정 (관리자만 가능)
    @PatchMapping("/{id}")
    public GenericResponse<CategoryResponse> updateCategory(
            @RequestBody CategoryRequest categoryRequest, @PathVariable(name = "id") Long id) {

        if (categoryRequest.getId() == null) {
            categoryRequest = CategoryRequest.builder()
                    .id(id) // PathVariable의 id 값을 넣음
                    .name(categoryRequest.getName())
                    // 필요한 다른 필드들도 builder에 추가
                    .build();
        }

        // 요청된 아이디가 일치하지 않으면 예외 발생
        if (!categoryRequest.getId().equals(id)) {
            throw new GlobalException(GlobalErrorCode.ID_MISMATCH);
        }

        // 카테고리 수정 로직 실행
        CategoryResponse categoryResponse = categoryService.updateCategory(
                CategoryConverter.changeEntity(categoryRequest));

        return GenericResponse.of(true, HttpStatus.OK.value(), categoryResponse);
    }
}

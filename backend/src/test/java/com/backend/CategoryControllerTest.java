package com.backend;

import static org.assertj.core.api.Assertions.assertThat;

import com.backend.domain.category.dto.response.CategoryResponse;
import com.backend.domain.category.entity.Category;
import com.backend.domain.category.repository.CategoryRepository;
import com.backend.domain.category.service.CategoryService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CategoryControllerTest {

    @Autowired
    private CategoryRepository categoryRepository; // Repository 주입

    @Autowired
    private CategoryService categoryService;

    @BeforeEach
    void setup() {
        // 테스트 데이터를 사전에 삽입
        categoryRepository.save(Category.builder()
                .name("Category 1")
                .build());

        categoryRepository.save(Category.builder()
                .name("Category 2")
                .build());
    }

    @Test
    void testCategoryList_shouldReturnCategoryDtos() {
        // 카테고리 전체 조회 (DTO 반환)
        List<CategoryResponse> categoryResponses = categoryService.categoryList();

        // 카테고리 전체 조회 결과가 2개인지 확인
        assertThat(categoryResponses.size()).isEqualTo(2);

        // 첫 번째 카테고리 DTO의 name이 "Category 1"인지 확인
        assertThat(categoryResponses.get(0).getName()).isEqualTo("Category 1");

        // 두 번째 카테고리 DTO의 name이 "Category 2"인지 확인
        assertThat(categoryResponses.get(1).getName()).isEqualTo("Category 2");
    }
}
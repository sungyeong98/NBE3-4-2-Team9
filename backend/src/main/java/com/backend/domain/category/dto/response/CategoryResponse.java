package com.backend.domain.category.dto.response;

import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponse {
    private Long id;
    private String name;
    private ZonedDateTime createdAt;
    private ZonedDateTime modifiedAt;

    public CategoryResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}

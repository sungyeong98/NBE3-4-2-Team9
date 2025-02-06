package com.backend.domain.category.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryRequest {
    @NotNull(message = "카테고리 이름은 필수입니다.")
    @NotBlank(message = "카테고리 이름을 입력해주세요.")
    private String name;

    private ZonedDateTime createdAt;
    private ZonedDateTime modifiedAt;

    public void setName(String name) {
        this.name = name;
    }
}

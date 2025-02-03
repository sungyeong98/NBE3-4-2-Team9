package com.backend.domain.board.entity;

import com.backend.domain.category.entity.Category;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@DiscriminatorValue(value = "Free")
public class FreeBoard extends Post {

    public FreeBoard(Long boardId, String subject, String content, Category categoryId) {
        super(boardId, subject, content, categoryId);
    }
}

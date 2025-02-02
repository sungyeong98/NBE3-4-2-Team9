package com.backend.domain.board.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue(value = "Free")
public class FreeBoard extends Post {

    public FreeBoard(Long boardId, String subject, String content) {
        super(boardId, subject, content);
    }
}

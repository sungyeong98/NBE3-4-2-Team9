package com.backend.domain.chat.dto.response;

import java.time.LocalDateTime;

import com.backend.domain.chat.entity.MessageType;

import lombok.Builder;

@Builder
public record ChatResponse(
        String userId,
        String username,
        MessageType type,
        LocalDateTime createdAt,
        String content
) {

}

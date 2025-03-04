package com.backend.domain.chat.dto.response;

import com.backend.domain.chat.entity.MessageType;

import lombok.Builder;

@Builder
public record ChatResponse(
        String userId,
        String username,
        MessageType type,
        String createdAt,
        String content
) {

}

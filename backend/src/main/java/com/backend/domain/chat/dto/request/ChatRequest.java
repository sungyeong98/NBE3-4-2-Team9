package com.backend.domain.chat.dto.request;

import com.backend.domain.chat.entity.MessageType;

import jakarta.validation.constraints.NotEmpty;

public record ChatRequest(
		@NotEmpty
		MessageType type,
		@NotEmpty
		String userId,
		@NotEmpty
		String content
) {

}
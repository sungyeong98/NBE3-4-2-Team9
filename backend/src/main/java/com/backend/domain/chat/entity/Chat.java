package com.backend.domain.chat.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.persistence.Column;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Document(collection = "chat")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Chat {

	@Id
	private String id;

	private String postId;
	private String userId;
	private String username;
	private String content;
	private MessageType type;

	@Column(name = "createdAt", updatable = false)
	private String createdAt;
}

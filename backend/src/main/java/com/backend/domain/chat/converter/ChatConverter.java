package com.backend.domain.chat.converter;

import com.backend.domain.chat.dto.request.ChatRequest;
import com.backend.domain.chat.dto.response.ChatResponse;
import com.backend.domain.chat.entity.Chat;

public class ChatConverter {

	/**
	 *  ChatRequest → Chat 변환 (빌더 패턴)
	 */
	public static Chat toChat(ChatRequest chatRequest, Long postId) {
		return Chat.builder()
			.postId(postId)
			.userId(chatRequest.userId())
			.content(chatRequest.content())
			.type(chatRequest.type())
			.build();
	}

	/**
	 *  Chat → ChatResponse 변환 (빌더 패턴)
	 */
	public static ChatResponse toChatResponse(Chat chat, String username, String profileImg) {
		return ChatResponse.builder()
			.userId(chat.getUserId())
			.username(username)
			.profileImg(profileImg)
			.content(chat.getContent())
			.type(chat.getType())
			.createdAt(chat.getCreatedAt())
			.build();
	}
}

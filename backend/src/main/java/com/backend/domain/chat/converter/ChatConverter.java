package com.backend.domain.chat.converter;

import com.backend.domain.chat.dto.request.ChatRequest;
import com.backend.domain.chat.dto.response.ChatResponse;
import com.backend.domain.chat.entity.Chat;
import com.backend.domain.user.entity.SiteUser;

public class ChatConverter {

	/**
	 *  ChatRequest → Chat 변환 (빌더 패턴)
	 */
	public static Chat toChat(ChatRequest chatRequest, String postId, SiteUser user){
		return Chat.builder()
			.postId(postId)
			.userId(chatRequest.userId())
			.username(user.getName())
			.content(chatRequest.content())
			.type(chatRequest.type())
			.build();
	}

	/**
	 *  Chat → ChatResponse 변환 (빌더 패턴)
	 */
	public static ChatResponse toChatResponse(Chat chat) {
		return ChatResponse.builder()
			.userId(chat.getUserId())
			.username(chat.getUsername())
			.content(chat.getContent())
			.type(chat.getType())
			.createdAt(chat.getCreatedAt())
			.build();
	}
}

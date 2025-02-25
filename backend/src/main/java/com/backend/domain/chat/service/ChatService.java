package com.backend.domain.chat.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.backend.domain.chat.converter.ChatConverter;
import com.backend.domain.chat.dto.request.ChatRequest;
import com.backend.domain.chat.dto.response.ChatResponse;
import com.backend.domain.chat.dto.response.ChatResponses;
import com.backend.domain.chat.entity.Chat;
import com.backend.domain.chat.repository.ChatRepository;
import com.backend.domain.user.entity.SiteUser;
import com.backend.domain.user.repository.UserRepository;
import com.backend.global.exception.GlobalErrorCode;
import com.backend.global.exception.GlobalException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatService {

	private final ChatRepository chatRepository;
	private final UserRepository userRepository;
	/**
	 * 채팅 저장
	 */
	public ChatResponse save(ChatRequest chatRequest, String postId) {
		SiteUser user = userRepository.findById(Long.valueOf(chatRequest.userId()))
			.orElseThrow(() -> new GlobalException(GlobalErrorCode.USER_NOT_FOUND));

		Chat chat = ChatConverter.toChat(chatRequest, postId, user);
		Chat savedChat = chatRepository.save(chat);

		return ChatConverter.toChatResponse(savedChat);
	}

	/**
	 * 채팅 조회
	 */

	public ChatResponses getAllByPostId(String postId) {
		List<ChatResponse> chats = chatRepository.findChatsByPost(postId)
			.stream()
			.map(ChatConverter::toChatResponse)
			.toList();
		return new ChatResponses(chats);
	}

}

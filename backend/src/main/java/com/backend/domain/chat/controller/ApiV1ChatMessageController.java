package com.backend.domain.chat.controller;

import java.util.Map;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.backend.domain.chat.dto.request.ChatRequest;
import com.backend.domain.chat.dto.response.ChatResponse;
import com.backend.domain.chat.entity.MessageType;
import com.backend.domain.chat.service.ChatService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/chat")
public class ApiV1ChatMessageController {

	private final ChatService chatService;

	@GetMapping()
	public String home() {
		return "index";
	}

	@MessageMapping("/msg/{postId}")
	@SendTo("/topic/{postId}")
	public ChatResponse sendMessage(
		@DestinationVariable Long postId,
		@Header("simpSessionAttributes") Map<String, Object> sessionAttributes,
		@Payload ChatRequest chatRequest
	) {
		// 메시지를 저장하고 응답을 반환
		return chatService.save(chatRequest, postId, sessionAttributes);
	}

	// TODO 채팅 입장, 퇴장 메세지 구현 필요
	@MessageMapping("/join/{postId}")
	@SendTo("/topic/{postId}")
	public ChatResponse handleJoin(
		@DestinationVariable Long postId,
		@Header("simpSessionAttributes") Map<String, Object> sessionAttributes
	) {
		String username = (String) sessionAttributes.get("username");
		Long userId = (Long) sessionAttributes.get("userId");

		ChatRequest chatRequest = new ChatRequest(
			MessageType.JOIN, userId, username + " 님이 입장했습니다."
		);

		log.info("join controller");
		return chatService.save(chatRequest, postId, sessionAttributes);
	}

	@MessageMapping("/leave/{postId}")
	@SendTo("/topic/{postId}")
	public ChatResponse handleLeave(
		@DestinationVariable Long postId,
		@Header("simpSessionAttributes") Map<String, Object> sessionAttributes
	) {
		String username = (String) sessionAttributes.get("username");
		Long userId = (Long) sessionAttributes.get("userId");

		ChatRequest chatRequest = new ChatRequest(
			MessageType.LEAVE, userId, username + " 님이 떠났습니다."
		);

		log.info("leave controller");
		return chatService.save(chatRequest, postId, sessionAttributes);
	}
}

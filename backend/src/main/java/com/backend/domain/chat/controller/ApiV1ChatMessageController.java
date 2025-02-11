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
		return "chat";
	}

	@MessageMapping("/msg/{postId}")
	@SendTo("/topic/{postId}")
	public ChatResponse sendMessage(
		@DestinationVariable Long postId,
		@Header("simpSessionAttributes") Map<String, Object> sessionAttributes,
		@Payload ChatRequest chatRequest
	) {

		return chatService.save(chatRequest, postId, sessionAttributes);
	}
}

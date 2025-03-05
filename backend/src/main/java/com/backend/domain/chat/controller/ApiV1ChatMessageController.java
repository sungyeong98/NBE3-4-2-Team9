package com.backend.domain.chat.controller;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.backend.domain.chat.dto.request.ChatRequest;
import com.backend.domain.chat.dto.response.ChatResponse;
import com.backend.domain.chat.service.ChatService;
import com.backend.global.redis.service.RedisPublisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/chat")
public class ApiV1ChatMessageController {

	private final ChatService chatService;
	private final RedisPublisher redisPublisher;

	@GetMapping()
	public String home() {
		return "mongo";
	}

	/**
	 * STOMP WebSocket을 통해 메시지를 받아서 처리
	 * - /pub/chat/{postId} 경로로 클라이언트가 메시지 전송
	 * - Redis Pub/Sub을 통해 다른 구독자들에게 전파
	 */
	@MessageMapping("/chat/{postId}")
	@SendTo("/sub/{postId}")
	public void sendMessage(
		@DestinationVariable String postId,
		@Payload ChatRequest chatRequest) {

		ChatResponse chatResponse = chatService.save(chatRequest, postId);
		redisPublisher.publish("postNum:" + postId, chatResponse);

		log.info("Message published to Redis: postNum:{}", postId);
	}
}


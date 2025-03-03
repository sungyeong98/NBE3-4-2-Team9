package com.backend.global.redis.service;

import java.io.IOException;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.backend.domain.chat.dto.response.ChatResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisSubscriber implements MessageListener {

	private final SimpMessagingTemplate messagingTemplate;
	private final ObjectMapper objectMapper;

	@Override
	public void onMessage(Message message, byte[] pattern) {

		String channel = new String(message.getChannel());
		log.info("Redis Subscriber - Received message from post: {}", channel);

		String postId = channel.replace("postNum:", "");

		ChatResponse chatResponse = null;
		try {
			chatResponse = objectMapper.readValue(message.getBody(), ChatResponse.class);
		} catch (IOException e) {
			log.error(e.getMessage());
		}

		if (chatResponse != null) {
			messagingTemplate.convertAndSend(String.format("/sub/%s", postId), chatResponse);
		} else {
			log.error("메세지 전송 실패");
		}
	}
}

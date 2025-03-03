package com.backend.global.redis.service;

import java.time.Duration;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.backend.domain.chat.dto.response.ChatResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisPublisher {

	private final RedisTemplate<String, Object> redisTemplate;

	public void publish(String postId, ChatResponse chatResponse) {
		String channel = "postNum:" + postId;
		log.info("Publishing message to Redis: {}", channel);

		// 메시지를 Redis Pub/Sub으로 전송
		redisTemplate.convertAndSend(channel, chatResponse);

		// Redis List에 채팅 메시지 저장 (채팅 로그 유지)
		redisTemplate.opsForList().leftPush("chatList:" + postId, chatResponse);
		redisTemplate.expire("chatList:" + postId, Duration.ofDays(1)); // 1일 후 자동 삭제
	}
}

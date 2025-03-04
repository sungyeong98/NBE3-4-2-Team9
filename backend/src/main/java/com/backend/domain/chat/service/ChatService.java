package com.backend.domain.chat.service;

import java.time.Duration;
import java.util.List;

import org.springframework.data.redis.core.RedisTemplate;
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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

	private final ChatRepository chatRepository;
	private final UserRepository userRepository;
	private final ObjectMapper objectMapper;
	private final RedisTemplate<String, Object> redisTemplate;

	private static final long ACTIVE_CHAT_TTL_MINUTES = 15;   // 활성 채팅방: 15분
	private static final long INACTIVE_CHAT_TTL_MINUTES = 60; // 비활성 채팅방: 60분 (1시간)
	private static final int CHAT_THRESHOLD = 50; // 메시지 개수 기준

	/**
	 *  채팅 저장 (DB에 저장 후 Redis에 추가)
	 */
	public ChatResponse save(ChatRequest chatRequest, String postId) {
		SiteUser user = userRepository.findById(Long.valueOf(chatRequest.userId()))
			.orElseThrow(() -> new GlobalException(GlobalErrorCode.USER_NOT_FOUND));

		Chat chat = ChatConverter.toChat(chatRequest, postId, user);
		Chat savedChat = chatRepository.save(chat);

		ChatResponse chatResponse = ChatConverter.toChatResponse(savedChat);

		// Redis에 저장 (JSON 변환 후 저장)
		try {
			String jsonChatResponse = objectMapper.writeValueAsString(chatResponse);
			redisTemplate.opsForList().leftPush("chatList:" + postId, jsonChatResponse);
			redisTemplate.expire("chatList:" + postId, Duration.ofMinutes(getDynamicTTL(postId)));
			log.info("채팅 저장 완료 및 Redis 캐싱 (TTL: {}분): postId = {}", getDynamicTTL(postId), postId);
		} catch (JsonProcessingException e) {
			log.error("Redis 저장 중 JSON 변환 오류", e);
		}

		return chatResponse;
	}

	/**
	 * 채팅 조회 (Redis에 없으면 DB에서 가져오고 Redis에 저장)
	 */
	public ChatResponses getAllByPostId(String postId) {
		// Redis에서 먼저 조회
		List<Object> chatList = redisTemplate.opsForList().range("chatList:" + postId, 0, -1);

		if (chatList != null && !chatList.isEmpty()) {
			log.info("From Redis (TTL: {}분): postId = {}", getDynamicTTL(postId), postId);
			List<ChatResponse> chats = chatList.stream()
				.map(obj -> {
					try {
						return objectMapper.readValue(obj.toString(), ChatResponse.class);
					} catch (JsonProcessingException e) {
						log.error("JSON 변환 오류", e);
						return null;
					}
				})
				.filter(chat -> chat != null) // null 값 제거
				.toList();
			return new ChatResponses(chats);
		}

		// Redis에 없으면 MongoDB에서 가져오기
		log.info("From MongoDB: postId = {}", postId);
		List<ChatResponse> chats = chatRepository.findChatsByPost(postId)
			.stream()
			.map(ChatConverter::toChatResponse)
			.toList();

		// 가져온 데이터 Redis에 저장 (개별 JSON 객체로 저장)
		if (!chats.isEmpty()) {
			chats.forEach(chat -> {
				try {
					String jsonChat = objectMapper.writeValueAsString(chat);
					redisTemplate.opsForList().rightPush("chatList:" + postId, jsonChat);
				} catch (JsonProcessingException e) {
					log.error("Redis 저장 중 JSON 변환 오류", e);
				}
			});
			redisTemplate.expire("chatList:" + postId, Duration.ofMinutes(getDynamicTTL(postId)));
			log.info("MongoDB -> Redis 캐싱 (TTL: {}분): postId = {}", getDynamicTTL(postId), postId);
		}

		return new ChatResponses(chats);
	}

	/**
	 * 채팅방 활성도를 기반으로 캐싱 시간 동적 처리
	 */
	private long getDynamicTTL(String postId) {
		Long messageCount = redisTemplate.opsForList().size("chatList:" + postId);
		if (messageCount == null)
			return INACTIVE_CHAT_TTL_MINUTES;
		return (messageCount >= CHAT_THRESHOLD) ? ACTIVE_CHAT_TTL_MINUTES : INACTIVE_CHAT_TTL_MINUTES;
	}
}

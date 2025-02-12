package com.backend.domain.chat.handler;

import java.util.Map;
import java.util.Objects;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import com.backend.domain.chat.exception.WebSocketException;
import com.backend.domain.recruitmentUser.repository.RecruitmentUserRepository;
import com.backend.domain.user.entity.SiteUser;
import com.backend.domain.user.repository.UserRepository;
import com.backend.global.exception.GlobalErrorCode;
import com.backend.global.exception.GlobalException;
import com.backend.standard.util.ExtractUtil;
import com.backend.standard.util.JwtUtil;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {

	public static final String DEFAULT_PATH = "/topic/";
	private final JwtUtil jwtUtil;
	private final UserRepository userRepository;
	private final RecruitmentUserRepository recruitmentUserRepository;

	// websocket 통해 들어온 요청이 처리 되기전 실행된다.
	@Override
	public Message<?> preSend(Message<?> message, MessageChannel channel) {

		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
		StompCommand command = accessor.getCommand();

		if (StompCommand.CONNECT.equals(command)) { // websocket 연결요청 -> JWT 인증
			log.info("{}", "CONNECT");
			// JWT 인증
			SiteUser user = getUserByAuthorizationHeader(
				accessor.getFirstNativeHeader("Authorization")
			);

			// 인증 후 데이터를 헤더에 추가
			setValue(accessor, "userId", user.getId());

			setValue(accessor, "username", user.getName());

			log.info("프로필값: {}", user.getProfileImg());

			if (user.getProfileImg() != null) {
				setValue(accessor, "profileImgUrl", user.getProfileImg());
				log.info("{}", "profileImgUrl 세팅");
			}
			log.info("커넥션 완료");

		} else if (StompCommand.SUBSCRIBE.equals(command)) { // 채팅룸
			log.info("{}", "SUBSCRIBE");

			Long userId = (Long)getValue(accessor, "userId");
			Long postId = parsePostIdFromPath(accessor);
			log.info("userId : {}, postId : {}", userId, postId);
			setValue(accessor, "postId", postId);
			// TODO 모집인원 검증
			// validateUserInPost(userId, postId);

		} else if (StompCommand.DISCONNECT == command) { // Websocket 연결 종료
			log.info("DISCONNECTED session : {}", accessor.getSessionId());
		}

		log.info("header : " + message.getHeaders());
		log.info("message:" + message);

		return message;
	}

	private SiteUser getUserByAuthorizationHeader(String authHeaderValue) {

		String accessToken = getTokenByAuthorizationHeader(authHeaderValue);

		Claims claims = jwtUtil.getClaims(accessToken);
		Long userId = claims.get("id", Long.class);

		return userRepository.findById(userId)
			.orElseThrow(() -> new GlobalException(GlobalErrorCode.USER_NOT_FOUND));
	}

	private String getTokenByAuthorizationHeader(String authHeaderValue) {

		if (Objects.isNull(authHeaderValue) || authHeaderValue.isBlank()) {
			throw new WebSocketException("authHeaderValue: " + authHeaderValue);
		}

		String accessToken = ExtractUtil.extractToken(authHeaderValue);
		log.info("accessToken value : {}", accessToken);

		jwtUtil.validateToken(accessToken);

		return accessToken;
	}

	private Long parsePostIdFromPath(StompHeaderAccessor accessor) {
		String destination = accessor.getDestination();
		log.info("{}", destination);
		return Long.parseLong(destination.substring(DEFAULT_PATH.length()));
	}

	private void validateUserInPost(Long userId, Long postId) {
		if (!recruitmentUserRepository.existsRecruitmentUserByPost_PostIdAndSiteUser_Id(postId, userId)) {
			throw new WebSocketException(String.format("현재 {} 모집게시판에 모집 인원이 아닙니다.", postId));
		}
	}

	private Object getValue(StompHeaderAccessor accessor, String key) {
		Map<String, Object> sessionAttributes = getSessionAttributes(accessor);
		Object value = sessionAttributes.get(key);

		if (Objects.isNull(value)) {
			throw new WebSocketException(key + " 값이 존재하지 않습니다.");
		}

		return value;
	}

	private void setValue(StompHeaderAccessor accessor, String key, Object value) {
		Map<String, Object> sessionAttributes = getSessionAttributes(accessor);
		sessionAttributes.put(key, value);
	}

	private Map<String, Object> getSessionAttributes(StompHeaderAccessor accessor) {
		Map<String, Object> sessionAttributes = accessor.getSessionAttributes();

		if (Objects.isNull(sessionAttributes)) {
			throw new WebSocketException("SessionAttributes -> null");
		}
		return sessionAttributes;
	}

}
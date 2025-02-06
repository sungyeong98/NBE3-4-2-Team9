package com.backend.domain.chat.handler;

import com.backend.domain.user.repository.UserRepository;
import com.backend.standard.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {

    public static final String DEFAULT_PATH = "/topic/";
    // TODO JWT 유저 검증 핸들러 작업
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    // TODO 모집 인원 구현시 검증 로직 추가
//    private final PostMemberRepository postMemberRepository;

    // websocket을 통해 들어온 요청이 처리 되기전 실행된다.
//    @Override
//    public Message<?> preSend(Message<?> message, MessageChannel channel) {
//
//        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
//        StompCommand command = accessor.getCommand();
//
//        if (StompCommand.CONNECT.equals(command)) { // websocket 연결요청 -> JWT 인증
//
//            // JWT 인증
//            SiteUser user = getUserByAuthorizationHeader(
//                    accessor.getFirstNativeHeader("Authorization"));
//            // 인증 후 데이터를 헤더에 추가
//            setValue(accessor, "userId", user.getId());
//            setValue(accessor, "username", user.getName());
//            setValue(accessor, "profileImgUrl", user.getProfileImg());
//
//        } else if (StompCommand.SUBSCRIBE.equals(command)) { // 채팅룸 구독요청(진입)
//
//            Long userId = (Long)getValue(accessor, "userId");
//            Long postId = parsePostIdFromPath(accessor);
//            log.debug("userId : " + userId + "postId : " + postId);
//            setValue(accessor, "postId", postId);
////            validateUserInPost(userId, postId);
//
//        } else if (StompCommand.DISCONNECT == command) { // Websocket 연결 종료
//            Long userId = (Long)getValue(accessor, "userId");
//            log.info("DISCONNECTED userId : {}", userId);
//        }
//
//        log.info("header : " + message.getHeaders());
//        log.info("message:" + message);
//
//        return message;
//    }
//
//    private SiteUser getUserByAuthorizationHeader(String authHeaderValue) {
//
//        String accessToken = getTokenByAuthorizationHeader(authHeaderValue);
//
//        Claims claims = jwtUtil.getClaims(accessToken);
//        Long userId = claims.get("userId", Long.class);
//
//        return userRepository.findById(userId)
//                .orElseThrow(() -> new GlobalException(GlobalErrorCode.USER_NOT_FOUND));
//    }
//
//    private String getTokenByAuthorizationHeader(String authHeaderValue) {
//
//        if (Objects.isNull(authHeaderValue) || authHeaderValue.isBlank()) {
//            throw new WebSocketException("authHeaderValue: " + authHeaderValue);
//        }
//
//        String accessToken = ExtractUtil.extractToken(authHeaderValue);
//        jwtUtil.validateToken(accessToken); // 예외 발생할 수 있음
//
//        return accessToken;
//    }
//
//    private Long parsePostIdFromPath(StompHeaderAccessor accessor) {
//        String destination = accessor.getDestination();
//        return Long.parseLong(destination.substring(DEFAULT_PATH.length()));
//    }
//
////    private void validateUserInPost(Long userId, Long postId) {
////        postMemberRepository.findPostMemberByPostIdAndUserId(postId, userId)
////                .orElseThrow(() -> new WebSocketException(
////                        String.format("post Id : {} userId : {} 로 조회된 결과가 없습니다.", postId, userId)));
////    }
//
//    private Object getValue(StompHeaderAccessor accessor, String key) {
//        Map<String, Object> sessionAttributes = getSessionAttributes(accessor);
//        Object value = sessionAttributes.get(key);
//
//        if (Objects.isNull(value)) {
//            throw new WebSocketException(key + " 에 해당하는 값이 없습니다.");
//        }
//
//        return value;
//    }
//
//    private void setValue(StompHeaderAccessor accessor, String key, Object value) {
//        Map<String, Object> sessionAttributes = getSessionAttributes(accessor);
//        sessionAttributes.put(key, value);
//    }
//
//    private Map<String, Object> getSessionAttributes(StompHeaderAccessor accessor) {
//        Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
//
//        if (Objects.isNull(sessionAttributes)) {
//            throw new WebSocketException("SessionAttributes가 null입니다.");
//        }
//        return sessionAttributes;
//    }

}
//package com.backend.domain.chat.handler;
//
//
//import com.backend.domain.chat.dto.request.ChatRequest;
//import com.backend.domain.chat.entity.MessageType;
//import com.backend.domain.chat.exception.WebSocketException;
//import java.util.Map;
//import java.util.Objects;
//import lombok.RequiredArgsConstructor;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.context.event.EventListener;
//import org.springframework.messaging.simp.SimpMessageSendingOperations;
//import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
//import org.springframework.stereotype.Component;
//import org.springframework.web.socket.messaging.SessionDisconnectEvent;
//import org.springframework.web.socket.messaging.SessionSubscribeEvent;
//
//@Component
//@RequiredArgsConstructor
//public class WebSocketEventListener {
//
//    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);
//
//    private final SimpMessageSendingOperations messagingTemplate;
//    //Todo StompHandler를 통한 검증 구현 후 동작
//    @EventListener
//    public void handleWebSocketSubscribeListener(SessionSubscribeEvent event) {
//        logger.info("Received a new web socket connection");
//        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
//
//        String username = (String)headerAccessor.getSessionAttributes().get("username");
//        Long userId = (Long)headerAccessor.getSessionAttributes().get("userId");
//        Long postId = (Long)headerAccessor.getSessionAttributes().get("postId");
//
//        if (username != null && userId != null && postId != null) {
//            logger.info("User: {} {} Disconnected Board : {}", userId, username, postId);
//            ChatRequest chatRequest = new ChatRequest(MessageType.JOIN, userId,
//                    username + " 님이 입장했습니다.");
//            messagingTemplate.convertAndSend("/topic/" + postId, chatRequest);
//        }
//    }
//
//    @EventListener
//    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
//        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
//
//        String username = (String)getValue(accessor, "username");
//        Long userId = (Long)getValue(accessor, "userId");
//        Long postId = (Long)getValue(accessor, "postId");
//
//        logger.info("User: {} {} Disconnected Board : {}", userId, username, postId);
//
//        ChatRequest chatRequest = new ChatRequest(
//                MessageType.LEAVE, userId, username + " 님이 떠났습니다.");
//
//        messagingTemplate.convertAndSend("/topic/" + postId, chatRequest);
//    }
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
//}

package com.backend.domain.chat.util;

import com.backend.domain.chat.entity.Chat;
import com.backend.domain.chat.entity.MessageType;
import java.util.List;
import java.util.stream.IntStream;

public class ChatObjectProvider {

    public static Chat getChat(Long postId, Long userId, String content) {
        return Chat.builder()
                .postId(postId)
                .userId(userId)
                .content(content)
                .type(MessageType.CHAT)
                .build();
    }

    public static List<Chat> getChatList(Long postId, Long userId) {

        return IntStream.range(0, 10)
                .mapToObj(i -> Chat.builder()
                        .userId(userId)
                        .postId(postId)
                        .content("content" + i)
                        .type(MessageType.CHAT)
                        .build()).toList();
    }
}
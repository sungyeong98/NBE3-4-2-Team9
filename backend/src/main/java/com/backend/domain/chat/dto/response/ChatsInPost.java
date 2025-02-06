package com.backend.domain.chat.dto.response;

import org.springframework.data.domain.Page;

public record ChatsInPost(
        Page<ChatResponse> chats
) {
}

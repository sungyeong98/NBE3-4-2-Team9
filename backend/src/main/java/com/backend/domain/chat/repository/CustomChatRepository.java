package com.backend.domain.chat.repository;

import java.util.List;

import com.backend.domain.chat.entity.Chat;

public interface CustomChatRepository {
	List<Chat> findChatsByPost(String postId);
}

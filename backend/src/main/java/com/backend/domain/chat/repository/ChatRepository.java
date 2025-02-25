package com.backend.domain.chat.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.backend.domain.chat.entity.Chat;

public interface ChatRepository extends MongoRepository<Chat, String> , CustomChatRepository{
}
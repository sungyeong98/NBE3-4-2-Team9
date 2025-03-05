package com.backend.domain.chat.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.backend.domain.chat.entity.Chat;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ChatRepositoryImpl implements CustomChatRepository {

	private final MongoTemplate mongoTemplate;

	@Override
	public List<Chat> findChatsByPost(String postId) {
		Query query = new Query()
			.addCriteria(Criteria.where("postId").is(postId))
			.with(Sort.by(Sort.Direction.ASC, "createdAt"));
		return mongoTemplate.find(query, Chat.class);
	}
}

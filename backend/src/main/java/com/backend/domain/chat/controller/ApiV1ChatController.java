package com.backend.domain.chat.controller;

import static org.springframework.http.MediaType.*;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.domain.chat.dto.response.ChatResponses;
import com.backend.domain.chat.service.ChatService;
import com.backend.global.response.GenericResponse;

import lombok.RequiredArgsConstructor;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chat")
public class ApiV1ChatController {

	private final ChatService chatService;

	@GetMapping(value = "/list/{postId}", produces = APPLICATION_JSON_VALUE)
	public GenericResponse<ChatResponses> getChattingList(
		@PathVariable(name = "postId") String postId) {

		ChatResponses result = chatService.getAllByPostId(postId);

		return GenericResponse.ok(result, "요청 성공");
	}

}


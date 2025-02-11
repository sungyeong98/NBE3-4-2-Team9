package com.backend.domain.chat.controller;

import static org.springframework.http.MediaType.*;

import java.util.Map;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.backend.domain.chat.dto.request.ChatRequest;
import com.backend.domain.chat.dto.response.ChatResponse;
import com.backend.domain.chat.dto.response.ChatResponses;
import com.backend.domain.chat.dto.response.ChatsInPost;
import com.backend.domain.chat.service.ChatService;
import com.backend.global.response.GenericResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/chat")
public class ChatV1Controller {

	private final ChatService chatService;

	@GetMapping()
	public String home() {
		return "index";
	}

	@ResponseBody
	@GetMapping(value = "/list/{postId}", produces = APPLICATION_JSON_VALUE)
	public GenericResponse<ChatResponses> getChattingList(
		@PathVariable(name = "postId") Long postId) {

		ChatResponses result = chatService.getAllByPostId(postId);
		return GenericResponse.of(true, HttpStatus.OK.value(), result, "요청 성공");
	}

	@ResponseBody
	@GetMapping(value = "/page/{postId}", produces = APPLICATION_JSON_VALUE)
	public GenericResponse<ChatsInPost> getChattingList(
		@PathVariable(name = "postId") Long postId,
		@PageableDefault(sort = "createdAt") Pageable pageable) {

		ChatsInPost result = chatService.getByPostId(postId, pageable);

		return GenericResponse.of(true, HttpStatus.OK.value(), result, "요청 성공");
	}

	@MessageMapping("/msg/{postId}")
	@SendTo("/topic/{postId}")
	public ChatResponse sendMessage(@DestinationVariable Long postId,
		@Header("simpSessionAttributes") Map<String, Object> sessionAttributes,
		@Payload ChatRequest chatRequest) {

		return chatService.save(chatRequest, postId, sessionAttributes);
	}

}


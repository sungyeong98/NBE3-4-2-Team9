package com.backend.domain.chat.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.backend.domain.chat.dto.response.ChatResponses;
import com.backend.domain.chat.dto.response.ChatsInPost;
import com.backend.domain.chat.service.ChatService;
import com.backend.global.response.GenericResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chat")
public class ApiV1ChatController {

	private final ChatService chatService;

	@GetMapping(value = "/list/{postId}", produces = APPLICATION_JSON_VALUE)
	public GenericResponse<ChatResponses> getChattingList(
		@PathVariable(name = "postId") Long postId) {

		ChatResponses result = chatService.getAllByPostId(postId);
		return GenericResponse.ok(result, "요청 성공");
	}

	@GetMapping(value = "/page/{postId}", produces = APPLICATION_JSON_VALUE)
	public GenericResponse<ChatsInPost> getChattingList(
		@PathVariable(name = "postId") Long postId,
		@RequestParam(value = "page", defaultValue = "0") int pageNum,
		@RequestParam(value = "size", defaultValue = "10") int pageSize) {

		ChatsInPost result = chatService.getByPostId(postId, PageRequest.of(pageNum, pageSize,
			Sort.by(Sort.Direction.ASC, "createdAt"))
		);

		return GenericResponse.ok(result, "요청 성공");
	}

}


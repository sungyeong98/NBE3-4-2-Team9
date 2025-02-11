package com.backend.domain.post.controller;

import com.backend.domain.post.dto.FreePostRequest;
import com.backend.domain.post.dto.PostCreateResponse;
import com.backend.domain.post.dto.PostResponse;
import com.backend.domain.post.service.FreePostService;
import com.backend.global.response.GenericResponse;
import com.backend.global.security.custom.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/free/posts")
public class ApiV1FreePostController {

	private final FreePostService freePostService;

	@GetMapping("/{postId}")
	public GenericResponse<PostResponse> findById(
		@PathVariable("postId") Long postId,
		@AuthenticationPrincipal CustomUserDetails customUserDetails) {

		PostResponse postResponse = freePostService.findById(postId, customUserDetails.getSiteUser());

		return GenericResponse.of(true, HttpStatus.OK.value(), postResponse);
	}

	@PostMapping
	public GenericResponse<PostCreateResponse> save(
		@Valid FreePostRequest freePostRequest,
		@AuthenticationPrincipal CustomUserDetails customUserDetails) {

		PostCreateResponse postCreateResponse = freePostService.save(freePostRequest,
			customUserDetails.getSiteUser());

		return GenericResponse.of(true, HttpStatus.CREATED.value(), postCreateResponse);
	}

	@PatchMapping("/{postId}")
	public GenericResponse<PostResponse> update(
		@PathVariable("postId") Long postId,
		@Valid FreePostRequest freePostRequest,
		@AuthenticationPrincipal CustomUserDetails customUserDetails) {

		PostResponse postResponse = freePostService.update(postId, freePostRequest,
			customUserDetails.getSiteUser());

		return GenericResponse.of(true, HttpStatus.OK.value(), postResponse);
	}

	@DeleteMapping("/{postId}")
	public GenericResponse<Void> delete(
		@PathVariable("postId") Long postId,
		@AuthenticationPrincipal CustomUserDetails customUserDetails) {

		freePostService.delete(postId, customUserDetails.getSiteUser());

		return GenericResponse.of(true, HttpStatus.OK.value());
	}
}

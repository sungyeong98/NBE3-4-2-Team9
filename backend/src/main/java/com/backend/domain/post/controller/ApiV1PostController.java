package com.backend.domain.post.controller;

import com.backend.domain.post.dto.PostPageResponse;
import com.backend.domain.post.service.PostService;
import com.backend.domain.post.util.PostSearchCondition;
import com.backend.global.response.GenericResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
public class ApiV1PostController {
	private final PostService postService;

	@GetMapping
	public GenericResponse<Page<PostPageResponse>> findAll(@Valid PostSearchCondition postSearchCondition) {

		Page<PostPageResponse> postPageResponsePage = postService.findAll(postSearchCondition);

		return GenericResponse.ok(postPageResponsePage);
	}

}

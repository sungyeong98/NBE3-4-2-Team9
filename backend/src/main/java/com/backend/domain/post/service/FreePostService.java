package com.backend.domain.post.service;

import com.backend.domain.category.domain.CategoryName;
import com.backend.domain.category.entity.Category;
import com.backend.domain.category.repository.CategoryRepository;
import com.backend.domain.post.conveter.PostConverter;
import com.backend.domain.post.dto.FreePostRequest;
import com.backend.domain.post.dto.PostCreateResponse;
import com.backend.domain.post.dto.PostResponse;
import com.backend.domain.post.entity.Post;
import com.backend.domain.post.repository.PostRepository;
import com.backend.domain.user.entity.SiteUser;
import com.backend.global.exception.GlobalErrorCode;
import com.backend.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FreePostService {

	private final PostRepository postRepository;
	private final CategoryRepository categoryRepository;

	@Transactional
	public PostCreateResponse save(FreePostRequest freePostRequest, SiteUser siteUser) {

		Category findCategory = categoryRepository.findByName(CategoryName.FREE.getValue())
			.orElseThrow(() -> new GlobalException(GlobalErrorCode.CATEGORY_NOT_FOUND));

		Post savePost = PostConverter
			.createPost(freePostRequest, siteUser, findCategory);

		Post savedPost = postRepository.save(savePost);

		return PostConverter.toPostCreateResponse(savedPost.getPostId(), findCategory.getId());
	}

	@Transactional
	public PostResponse update(Long postId, FreePostRequest freePostRequest, SiteUser siteUser) {

		Post target = postRepository.findById(postId)
			.orElseThrow(() -> new GlobalException(GlobalErrorCode.POST_NOT_FOUND));

		if (target.getAuthor().getId().equals(siteUser.getId())) {
			throw new GlobalException(GlobalErrorCode.POST_NOT_AUTHOR);
		}

		target.updatePost(freePostRequest.getSubject(), freePostRequest.getContent());

		Post updatedPost = postRepository.save(target);

		return PostConverter.toPostResponse(updatedPost, true);
	}

	@Transactional
	public void delete(Long postId, SiteUser siteUser) {

		Post findPost = postRepository.findById(postId)
			.orElseThrow(() -> new GlobalException(GlobalErrorCode.POST_NOT_FOUND));

		if (!findPost.getAuthor().getId().equals(siteUser.getId())) {
			throw new GlobalException(GlobalErrorCode.POST_NOT_AUTHOR);
		}

		postRepository.deleteById(findPost.getPostId());
	}

}

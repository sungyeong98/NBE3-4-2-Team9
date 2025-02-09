package com.backend.domain.comment.service;

import com.backend.domain.comment.dto.request.CommentRequestDto;
import com.backend.domain.comment.dto.response.CommentCreateResponseDto;
import com.backend.domain.comment.entity.Comment;
import com.backend.domain.comment.repository.CommentRepository;
import com.backend.domain.post.entity.Post;
import com.backend.domain.post.repository.PostRepository;
import com.backend.domain.user.entity.SiteUser;
import com.backend.domain.user.repository.UserRepository;
import com.backend.global.exception.GlobalErrorCode;
import com.backend.global.exception.GlobalException;
import com.backend.global.security.custom.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public CommentCreateResponseDto createComment(CommentRequestDto dto, Long postId,
        CustomUserDetails user) {

        // 게시글정보가 db에 있는지에 대한 검증
        Post findPost = postRepository.findById(postId).orElseThrow(
            () -> new GlobalException(GlobalErrorCode.POST_NOT_FOUND)
        );

        // 사용자정보가 db에 있는지에 대한 검증
        SiteUser findUser = userRepository.findById(user.getSiteUser().getId()).orElseThrow(
            () -> new GlobalException(GlobalErrorCode.USER_NOT_FOUND)
        );

        Comment comment = Comment.builder()
            .content(dto.getContent())
            .post(findPost)
            .siteUser(findUser)
            .build();

        commentRepository.save(comment);

        return CommentCreateResponseDto.convertEntity(comment);
    }


}

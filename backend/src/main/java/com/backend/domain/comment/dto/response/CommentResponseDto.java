package com.backend.domain.comment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponseDto {

    /**
     * {
     * "id": 1,
     * "content": "작성한 댓글 내용",
     * "createdAt": "2025-02-07T14:27:28.271879+09:00",
     * "modifiedAt": "2025-02-07T14:27:28.271879+09:00",
     * "profileImageUrl" : url
     * "authorName" : 작성자 이름
     * }
     */

    private Long id;
    private String content;
    private ZonedDateTime createdAt;
    private ZonedDateTime modifiedAt;
    private String profileImageUrl;
    private String authorName;
    private boolean isAuthor;

    public CommentResponseDto(Long id, String content, ZonedDateTime createdAt, ZonedDateTime modifiedAt, String profileImageUrl, String authorName) {
        this.id = id;
        this.content = content;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
        this.profileImageUrl = profileImageUrl;
        this.authorName = authorName;
    }

    public void setIsAuthor(boolean isAuthor) {
        this.isAuthor = isAuthor;
    }
}

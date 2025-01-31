package com.backend.domain.board.repository;

import com.backend.domain.board.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {

    // 제목이나 게시글 내용에 keyword 포함하여 검색
    Page<Post> findBySubjectContainingOrContentContaining(String subject, String content, Pageable pageable);

    // TODO: 카테고리별 게시글 조회
}

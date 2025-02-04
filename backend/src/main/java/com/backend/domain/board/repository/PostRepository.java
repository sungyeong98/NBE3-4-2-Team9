package com.backend.domain.board.repository;

import com.backend.domain.board.entity.Post;
import com.backend.domain.board.entity.PostType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;



public interface PostRepository extends JpaRepository<Post, Long> {

    // 카테고리와 제목, 내용에서 검색어 포함 여부를 처리하는 쿼리
    @Query("SELECT p FROM Post p WHERE " +
            "(:categoryId IS NULL OR p.categoryId.id = :categoryId) AND " +
            "(:keyword IS NULL OR p.subject LIKE %:keyword% OR p.content LIKE %:keyword%) AND " +
            "(:postType IS NULL OR p.postType = :postType)")
    Page<Post> findByCategoryAndKeywordAndPostType(@Param("categoryId") Long categoryId,
            @Param("keyword") String keyword,
            @Param("postType") PostType postType,
            Pageable pageable);


    // 제목 또는 내용에서 검색어만 검색하는 쿼리
    @Query("SELECT p FROM Post p WHERE " +
            "(:keyword IS NULL OR p.subject LIKE %:keyword% OR p.content LIKE %:keyword%) AND " +
            "(:postType IS NULL OR p.postType = :postType)")
    Page<Post> findByKeywordAndPostType(@Param("keyword") String keyword,
            @Param("postType") PostType postType,
            Pageable pageable);


    // 전체 게시글 조건 없이 조회
    @Query("SELECT p FROM Post p WHERE (:postType IS NULL OR p.postType = :postType)")
    Page<Post> findAllByPostType(@Param("postType") PostType postType, Pageable pageable);

}

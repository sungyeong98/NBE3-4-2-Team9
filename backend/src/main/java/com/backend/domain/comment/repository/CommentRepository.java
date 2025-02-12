package com.backend.domain.comment.repository;

import com.backend.domain.comment.dto.response.CommentResponseDto;
import com.backend.domain.comment.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("SELECT c FROM Comment c JOIN FETCH c.siteUser WHERE c.id = :commentId")
    Optional<Comment> findById(@Param("commentId") Long commentId);

    Page<Comment> findByPost_PostId(@Param("postId") Long postId, Pageable pageable);

    @Query("""
            SELECT new com.backend.domain.comment.dto.response.CommentResponseDto(
                        c.id, c.content, c.createdAt, c.modifiedAt, c.siteUser.profileImg, c.siteUser.name
            )
            FROM Comment c
            LEFT JOIN c.siteUser
            LEFT JOIN c.post
            WHERE c.post.postId = :postId
            """)
    Page<CommentResponseDto> findAllByPostId(@Param("postId") Long postId, Pageable pageable);

    @Query("""
            SELECT c
            FROM Comment c
            LEFT JOIN FETCH c.siteUser
            LEFT JOIN FETCH c.post
            WHERE c.post.postId = :postId
            """)
    Page<Comment> findAll(@Param("postId") Long postId, Pageable pageable);


    /**
     * SiteUser id, email, name, age, address
     * join fetch 사용시 -> SiteUser id, email, name, age, address 모든 필드가 초기화 됌
     * join 을 사용할 시 -> 내가 선택한 필드만 조회 2개만 -> 엔티티로 반환
     *
     * JPA는 join을 사용해도 엔티티 로딩 전략이 LAZY면 무조건 지연로딩을 사용하게 함
     * 또다시 유저 정보를 get하는 순간 쿼리 발생 (N+1)
     *
     * DTO로 조회하는 이유는 join fetch로 엔티티 조회할 때 모든 필드를 조회하기 때문에 성능 개선을 위해서
     * 그냥 편합니다.
     *
     * 연관관계가 게시글에 총 4개
     * 게시글이 총 3000개
     * 3000번 조회 = 3000 * 4 = 12000 쿼리가 즉 배로 증가하는건데
     * 몇개의 쿼리가 더 날라갈지는 모르는거죠
     *
     *
     * EAGER 조회는 편한데 필요 없는 데이터도 항상 가지고 옴
     * LAZY 조회는 힘든데 단점 N+1, 장점이라고 하면
     * 네트워크 연결해서 query 보내고 그거에 대한 응답을 받아오는거 응답 값이 커지면 커질수록 부하
     * 성능적으로도 좋지 않을테고
     * 사용자가 응답 받는게 늦어진다.
     *
     */

}

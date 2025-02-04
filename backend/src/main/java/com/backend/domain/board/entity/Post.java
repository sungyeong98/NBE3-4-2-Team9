package com.backend.domain.board.entity;

import com.backend.domain.category.entity.Category;
import com.backend.global.baseentity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.ZonedDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 게시판 엔티티
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="Post")
@AllArgsConstructor
@Builder
public class Post extends BaseEntity {

    // board_id: 게시글의 고유 식별자(PK, Auto Increment)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boardId;

    // subject: 게시글 제목
    @Column(nullable = false)
    private String subject;

    // content: 게시글 내용
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    // 카테고리 ID -> 카테고리 테이블과의 관계 설정 | 다대일 관계, board의 여러 게시글이 하나의 카테고리 참조
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category categoryId;

    // 모집 게시판에만 필요한 부분
    private ZonedDateTime recruimentClosingDate; // 모집 기간
    private Long numOfApplicants; // 모집 인원

    @Enumerated(EnumType.STRING)
    @Column(nullable = true) // 모집 게시판 아니면 Null 가능
    private RecruitmentStatus recruitmentStatus; // 모집 상태

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PostType postType; // 게시글 종류

    // 채용 ID -> JopPosting table에 채용ID랑 이어짐
    // TODO: jobposting 미구현, 구현 이후 다시 작업
//    @ManyToOne
//    @JoinColumn(name = "job_id", nullable = false)
//    private JobPosting jobId;

    // createDate: 생성일자, BaseEntity 상속
    // modifyDate: 수정일자, BaseEntity 상속


    // 게시글 수정
    public void updatePost(String subject, String content) {
        // 기존 제목과 다를 때
        this.subject = subject;
        // 기존 게시글 내용과 다를 때
        this.content = content;
    }

    // 테스트용 생성자 추가
    public Post(String subject, String content, PostType postType, Category category) {
        this.subject = subject;
        this.content = content;
        this.postType = postType;
        this.categoryId = category;

        // 모집 게시판이 아닐 경우 기본값 설정
        if (postType == PostType.RECRUITMENT) {
            this.recruitmentStatus = RecruitmentStatus.OPEN; // 기본값 OPEN 설정
        } else {
            this.recruitmentStatus = null; // 자유 게시판이면 NULL 가능
        }
    }

}
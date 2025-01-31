package com.backend.domain.board.entity;

import com.backend.global.baseentity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


// 게시판 엔티티
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name="Board")
public class Post extends BaseEntity {

    // board_id: 게시글의 고유 식별자(PK, Auto Increment)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long board_id;

    // subject: 게시글 제목
    @Column(nullable = false)
    private String subject;

    // content: 게시글 내용
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    // category: 카테고리
    // 게시글의 카테고리 이름을 저장하는 필드
    @Column(nullable = false)
    private String category;

    // createDate: 생성일자, BaseEntity 상속
    // modifyDate: 수정일자, BaseEntity 상속

    // 카테고리 ID -> 카테고리 테이블과의 관계 설정 | 다대일 관계, board의 여러 게시글이 하나의 카테고리 참조
    // TODO: category, jobposting 미구현, 구현 이후 다시 작업
//    @ManyToOne
//    @JoinColumn(name = "category_id", nullable = false)
//    private Category categoryId;

    // 채용 ID -> JopPosting table에 채용ID랑 이어짐
//    @ManyToOne
//    @JoinColumn(name = "job_id", nullable = false)
//    private JobPosting jobId;

}
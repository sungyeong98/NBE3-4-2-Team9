package com.backend.domain.board.entity;

import com.backend.global.baseentity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


// 게시판 엔티티
@Entity
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
    @Column(nullable = false)
    private String category;

    // createDate: 생성일자, BaseEntity 상속
    // modifyDate: 수정일자, BaseEntity 상속

    // 카테고리 ID -> 카테고리 테이블이랑 이어짐
    // 다대일 관계, board의 여러 게시글이 하나의 카테고리 참조
//    @ManyToOne
//    @JoinColumn(name = "category_id", nullable = false)
//    private Category category_id;

    // 채용 ID -> JopPosting table에 채용ID랑 이어짐
//    @ManyToOne
//    @JoinColumn(name = "job_id", nullable = false)
//    private JobPosting job_id;

}
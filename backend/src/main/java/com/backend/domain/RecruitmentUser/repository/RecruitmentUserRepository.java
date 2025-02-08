package com.backend.domain.RecruitmentUser.repository;

import com.backend.domain.RecruitmentUser.entity.RecruitmentUser;
import com.backend.domain.RecruitmentUser.entity.RecruitmentUserStatus;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 모집 지원자 관련 데이터 처리 리포지토리
 */
public interface RecruitmentUserRepository extends JpaRepository<RecruitmentUser, Long> {

    /**
     * 특정 사용자가 특정 모집 게시글에 지원한 내역 조회
     *
     * @param postId 모집 게시글 ID
     * @param userId 사용자 ID
     * @return 모집 지원 내역 (존재하지 않을 경우 빈 Optional 반환)
     */
    Optional<RecruitmentUser> findByPost_PostIdAndSiteUser_Id(Long postId, Long userId);

    /**
     * 특정 모집 게시글에 대해 지정된 상태의 모집 지원자 목록 조회
     *
     * @param postId 모집 게시글 ID
     * @param status 조회할 모집 지원 상태 (예: APPLIED, ACCEPTED, REJECTED 등)
     * @return 해당 상태의 모집 지원자 목록
     */
    Page<RecruitmentUser> findAllByPost_PostIdAndStatus(Long postId, RecruitmentUserStatus status, Pageable pageable);

    /**
     * 특정 사용자가 지정된 상태로 모집된 게시글 목록을 페이지네이션하여 조회
     *
     * @param userId   사용자 ID
     * @param status   모집 지원 상태 (예: APPLIED, ACCEPTED, REJECTED 등)
     * @param pageable 페이지네이션 정보
     * @return 모집된 게시글 목록 (페이징 결과)
     */
    Page<RecruitmentUser> findAllBySiteUser_IdAndStatus(Long userId, RecruitmentUserStatus status, Pageable pageable);
}

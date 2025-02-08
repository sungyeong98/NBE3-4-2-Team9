package com.backend.domain.RecruitmentUser.dto.response;

import com.backend.domain.RecruitmentUser.entity.RecruitmentUser;
import org.springframework.data.domain.Page;

/**
 * 모집 지원자 목록을 포함하는 DTO (페이징 적용)
 *
 * @param postId              모집 게시글 ID
 * @param recruitmentUserList 모집 지원자 목록 (페이징 지원)
 */
public record RecruitmentUserPageResponse(
        Long postId,
        Page<RecruitmentUserDetail> recruitmentUserList
) {

    /**
     * 모집 지원자 목록을 Page<RecruitmentUserDetail> 형태로 변환
     */
    public static RecruitmentUserPageResponse from(Long postId,
            Page<RecruitmentUser> recruitmentUsers) {
        return new RecruitmentUserPageResponse(postId,
                recruitmentUsers.map(RecruitmentUserDetail::from));
    }
}

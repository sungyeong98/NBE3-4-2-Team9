package com.backend.domain.RecruitmentUser.controller;

import com.backend.domain.RecruitmentUser.dto.request.AuthorRequest;
import com.backend.domain.RecruitmentUser.dto.response.RecruitmentUserPageResponse;
import com.backend.domain.RecruitmentUser.service.RecruitmentAuthorService;
import com.backend.domain.user.entity.SiteUser;
import com.backend.global.response.GenericResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/recruitment-author")
public class ApiV1RecruitmentAuthorController {

    private final RecruitmentAuthorService recruitmentAuthorService;

    /**
     * 모집 지원 승인 작성자가 특정 지원자의 모집 신청을 승인합니다.
     *
     * @param author  현재 로그인한 작성자
     * @param postId  모집 게시글 ID
     * @param request 모집 승인 요청 (userId 포함)
     * @return 성공 응답 (200 OK)
     */
    @PostMapping("/{postId}/accept")
    public GenericResponse<Void> approveRecruitment(
            @AuthenticationPrincipal SiteUser author,
            @PathVariable Long postId,
            @RequestBody @Valid AuthorRequest request) {

        recruitmentAuthorService.recruitmentAccept(author, postId, request.userId());
        return GenericResponse.of(true, HttpStatus.OK.value());
    }

    /**
     * 모집 지원 거절 작성자가 특정 지원자의 모집 신청을 거절합니다.
     *
     * @param author  현재 로그인한 작성자
     * @param postId  모집 게시글 ID
     * @param request 모집 거절 요청 (userId 포함)
     * @return 성공 응답 (200 OK)
     */
    @PostMapping("/{postId}/reject")
    public GenericResponse<Void> rejectRecruitment(
            @AuthenticationPrincipal SiteUser author,
            @PathVariable Long postId,
            @RequestBody @Valid AuthorRequest request) {

        recruitmentAuthorService.recruitmentReject(author, postId, request.userId());
        return GenericResponse.of(true, HttpStatus.OK.value());
    }

    /**
     * 모집 지원자 목록 조회 작성자가 본인의 모집 게시글에 지원한 사용자 목록을 조회합니다.
     *
     * @param author   현재 로그인한 작성자
     * @param postId   모집 게시글 ID
     * @param pageNum  페이지 번호 (기본값: 0)
     * @param pageSize 페이지 크기 (기본값: 10)
     * @return 지원자 목록 (DTO 변환)
     */
    @GetMapping("/{postId}/applied-users")
    public GenericResponse<RecruitmentUserPageResponse> getAppliedUsers(
            @AuthenticationPrincipal SiteUser author,
            @PathVariable Long postId,
            @RequestParam(defaultValue = "0") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        Pageable pageable = PageRequest.of(pageNum, pageSize,
                Sort.by(Sort.Direction.ASC, "createdAt"));

        RecruitmentUserPageResponse appliedUsers = recruitmentAuthorService.getAppliedUserList(
                author, postId, pageable);
        return GenericResponse.of(true, HttpStatus.OK.value(), appliedUsers);
    }

    /**
     * 모집 승인된 참여자 목록 조회 모집이 완료된 후 승인된 지원자 목록을 조회합니다.
     *
     * @param author   현재 로그인한 작성자
     * @param postId   모집 게시글 ID
     * @param pageNum  페이지 번호 (기본값: 0)
     * @param pageSize 페이지 크기 (기본값: 10)
     * @return 모집된 참여자 목록 (DTO 변환)
     */
    @GetMapping("/{postId}/accepted-users")
    public GenericResponse<RecruitmentUserPageResponse> getAcceptedUsers(
            @AuthenticationPrincipal SiteUser author,
            @PathVariable Long postId,
            @RequestParam(defaultValue = "0") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        Pageable pageable = PageRequest.of(pageNum, pageSize,
                Sort.by(Sort.Direction.ASC, "createdAt"));

        RecruitmentUserPageResponse acceptedUsers = recruitmentAuthorService.getAcceptedUserList(
                author, postId, pageable);
        return GenericResponse.of(true, HttpStatus.OK.value(), acceptedUsers);
    }
}

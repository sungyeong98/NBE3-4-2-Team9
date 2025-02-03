package com.backend.domain.user.controller;

import com.backend.domain.user.dto.request.UserModifyProfileRequest;
import com.backend.domain.user.dto.response.UserGetProfileResponse;
import com.backend.domain.user.entity.SiteUser;
import com.backend.domain.user.service.UserService;
import com.backend.global.exception.GlobalErrorCode;
import com.backend.global.exception.GlobalException;
import com.backend.global.response.GenericResponse;
import com.backend.global.security.custom.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ApiV1UserController {

    private final UserService userService;

    @GetMapping("/users/{user_id}")
    public GenericResponse<UserGetProfileResponse> getProfile(
            @PathVariable Long user_id,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
            ) {
        //  user_id의 존재 유무를 확인하기 위한 코드
        userService.getUserById(user_id);

        // 비회원의 접근시
        if (customUserDetails == null) {
            throw new GlobalException(GlobalErrorCode.USER_NOT_FOUND);
        }

        // 로그인한 회원의 id와 user_id가 다를시
        if (!user_id.equals(customUserDetails.getSiteUser().getId())) {
            throw new GlobalException(GlobalErrorCode.UNAUTHORIZATION_USER);
        }

        return GenericResponse.of(
                true,
                HttpStatus.OK.value(),
                new UserGetProfileResponse(customUserDetails.getSiteUser())
        );
    }
//
//    @PatchMapping("/users/{user_id}")
//    @Transactional
//    public GenericResponse<Void> modifyProfile(
//            @PathVariable Long user_id,
//            @RequestBody UserModifyProfileRequest req
//    ) {
//        SiteUser siteUser = userService.getUserById(user_id);
//
//        // TODO (유저 인증 코드 추가 예정)
//
//        userService.modifyUser(siteUser, req);
//
//        return GenericResponse.of(
//                true,
//                200
//        );
//    }

}

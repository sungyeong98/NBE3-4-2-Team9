package com.backend.domain.user.controller;

import com.backend.domain.user.dto.request.UserModifyProfileRequest;
import com.backend.domain.user.dto.response.UserGetProfileResponse;
import com.backend.domain.user.service.UserService;
import com.backend.global.response.GenericResponse;
import com.backend.global.security.custom.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ApiV1UserController {

    private final UserService userService;

    /**
     * 유저 정보를 출력하기 위한 메서드 입니다.
     *
     * @param userId 유저 고유 식별 id
     * @param customUserDetails
     * @return {@link GenericResponse<UserGetProfileResponse>}
     */
    @GetMapping("/users/{user_id}")
    public GenericResponse<UserGetProfileResponse> getProfile(
            @PathVariable(name = "user_id") Long userId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
            ) {
        return GenericResponse.of(
                true,
                HttpStatus.OK.value(),
                new UserGetProfileResponse(userService.getUser(userId, customUserDetails))
        );
    }

    /**
     * 유저 정보를 수정하기 위한 메서드 입니다.
     *
     * @param userId 유저 고유 식별 id
     * @param req 유저 프로필 수정 DTO
     * @param customUserDetails
     * @return {@link GenericResponse<Void>}
     */
    @PatchMapping("/users/{user_id}")
    public GenericResponse<Void> modifyProfile(
            @PathVariable(name = "user_id") Long userId,
            @RequestBody UserModifyProfileRequest req,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        userService.modifyUser(userId, customUserDetails, req);

        return GenericResponse.of(
                true,
                HttpStatus.OK.value()
        );
    }

}

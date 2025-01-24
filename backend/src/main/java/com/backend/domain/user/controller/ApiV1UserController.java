package com.backend.domain.user.controller;

import com.backend.domain.user.dto.request.UserModifyProfileRequest;
import com.backend.domain.user.dto.response.UserGetProfileResponse;
import com.backend.domain.user.entity.SiteUser;
import com.backend.domain.user.service.UserService;
import com.backend.global.response.GenericResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ApiV1UserController {

    private final UserService userService;

    @GetMapping("/users/{user_id}")
    @Transactional(readOnly = true)
    public GenericResponse<UserGetProfileResponse> getProfile(
            @PathVariable Long user_id
    ) {
        SiteUser siteUser = userService.getUserById(user_id);

        // TODO (유저 인증 코드 추가 예정)

        return GenericResponse.of(
                true,
                "200",
                new UserGetProfileResponse(siteUser)
        );
    }

    @PatchMapping("/users/{user_id}")
    @Transactional
    public GenericResponse<UserModifyProfileRequest> modifyProfile(
            @PathVariable Long user_id,
            @RequestBody UserModifyProfileRequest req
    ) {
        SiteUser siteUser = userService.getUserById(user_id);

        // TODO (유저 인증 코드 추가 예정)

        userService.modifyUser(siteUser, req);

        return GenericResponse.of(
                true,
                "200"
        );
    }

}

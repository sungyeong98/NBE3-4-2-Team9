package com.backend.domain.user.controller;

import com.backend.domain.user.dto.request.AdminLoginRequest;
import com.backend.domain.user.dto.request.UserModifyProfileRequest;
import com.backend.domain.user.dto.response.UserGetProfileResponse;
import com.backend.domain.user.entity.SiteUser;
import com.backend.domain.user.entity.UserRole;
import com.backend.domain.user.service.AuthTokenService;
import com.backend.domain.user.service.UserService;
import com.backend.global.exception.GlobalErrorCode;
import com.backend.global.exception.GlobalException;
import com.backend.global.response.GenericResponse;
import com.backend.global.rq.Rq;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ApiV1UserController {

    private final UserService userService;
    private final Rq rq;
    private final AuthTokenService authTokenService;

    @PostMapping("/adm/login")
    @Transactional(readOnly = true)
    public GenericResponse<Void> admLogin(
            @RequestBody AdminLoginRequest req
    ) {
        SiteUser siteUser = userService.findByEmail(req.getEmail()).orElseThrow(
                () -> new GlobalException(GlobalErrorCode.USER_NOT_FOUND)
        );

        if (!siteUser.getUserRole().equals(UserRole.ROLE_ADMIN)) {
            throw new GlobalException(GlobalErrorCode.UNAUTHORIZATION_USER);
        }

        String accessToken = userService.genAccessToken(siteUser);
        rq.setCookie("accessToken", accessToken);
        rq.setCookie("apiKey", siteUser.getApiKey());

        return GenericResponse.of(
                true,
                "200"
        );
    }

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
    public GenericResponse<Void> modifyProfile(
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

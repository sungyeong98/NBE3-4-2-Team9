package com.backend.domain.user.service;

import com.backend.domain.user.dto.request.UserModifyProfileRequest;
import com.backend.domain.user.entity.SiteUser;
import com.backend.domain.user.repository.UserRepository;
import com.backend.global.exception.GlobalErrorCode;
import com.backend.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public SiteUser getUserById(long id) {
        SiteUser siteUser = userRepository.findById(id).orElseThrow(
                () -> new GlobalException(GlobalErrorCode.USER_NOT_FOUND)
        );

        // TODO (사용자 계저 활성화 상태 점검 코드 추가 예정)

        return siteUser;
    }

    public void modifyUser(SiteUser siteUser, UserModifyProfileRequest req) {
        siteUser.modifyProfile(req.getIntroduction(), req.getJob(), req.getSkill());
    }

    public Optional<SiteUser> findById(long id) {
        return userRepository.findById(id);
    }

}

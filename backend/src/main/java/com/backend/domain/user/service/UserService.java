package com.backend.domain.user.service;

import com.backend.domain.jobskill.entity.JobSkill;
import com.backend.domain.jobskill.repository.JobSkillRepository;
import com.backend.domain.user.dto.request.UserModifyProfileRequest;
import com.backend.domain.user.dto.response.UserGetProfileResponse;
import com.backend.domain.user.entity.SiteUser;
import com.backend.domain.user.repository.UserRepository;
import com.backend.global.exception.GlobalErrorCode;
import com.backend.global.exception.GlobalException;
import com.backend.global.security.custom.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final JobSkillRepository jobSkillRepository;

    /**
     * 유저 id를 찾기 위한 메서드 입니다.
     *
     * @param id
     * @return {@link SiteUser}
     */
    public SiteUser getUserById(long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new GlobalException(GlobalErrorCode.USER_NOT_FOUND)
        );
    }

    /**
     * 유저 검증을 위한 메서드 입니다.
     *
     * @param id
     * @param customUserDetails
     *
     */
    public void isValidUser(long id, CustomUserDetails customUserDetails) {
        if (customUserDetails == null) {
            throw new GlobalException(GlobalErrorCode.USER_NOT_FOUND);
        }

        if (id != customUserDetails.getSiteUser().getId()) {
            throw new GlobalException(GlobalErrorCode.UNAUTHORIZATION_USER);
        }
    }

    /**
     * 유저 정보를 가져오기 위한 메서드 입니다.
     *
     * @param id
     * @param customUserDetails
     * @return {@link SiteUser}
     */
    public SiteUser getUser(long id, CustomUserDetails customUserDetails) {
        isValidUser(id, customUserDetails);

        SiteUser user = getUserById(id);

        return user;
    }

    /**
     * 유저 정보를 수정하기 위한 메서드 입니다.
     *
     * @param id
     * @param customUserDetails
     * @param req
     */
    @Transactional
    public void modifyUser(long id, CustomUserDetails customUserDetails, UserModifyProfileRequest req) {
        isValidUser(id, customUserDetails);

        SiteUser user = getUserById(id);

        if (req.getJobSkills() != null) {
            user.getJobSkills().clear();

            req.getJobSkills().forEach(jobSkillReq -> {
                JobSkill jobSkill = jobSkillRepository.findByName(jobSkillReq.getName())
                        .orElseThrow(() -> new GlobalException(GlobalErrorCode.INVALID_JOB_SKILL));
                user.getJobSkills().add(jobSkill);
            });
        }

        user.modifyProfile(req.getIntroduction(), req.getJob());
    }

}

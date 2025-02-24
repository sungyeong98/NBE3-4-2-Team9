package com.backend.domain.user.service;

import com.backend.domain.jobskill.entity.JobSkill;
import com.backend.domain.jobskill.repository.JobSkillRepository;
import com.backend.domain.user.dto.request.JobSkillRequest;
import com.backend.domain.user.dto.request.UserModifyProfileRequest;
import com.backend.domain.user.entity.SiteUser;
import com.backend.domain.user.entity.UserRole;
import com.backend.domain.user.repository.UserRepository;
import com.backend.global.exception.GlobalErrorCode;
import com.backend.global.exception.GlobalException;
import com.backend.global.security.custom.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final JobSkillRepository jobSkillRepository;

    public SiteUser getUserById(long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new GlobalException(GlobalErrorCode.USER_NOT_FOUND)
        );
    }

    public void isValidUser(long id, CustomUserDetails customUserDetails) {
        if (customUserDetails == null) {
            throw new GlobalException(GlobalErrorCode.USER_NOT_FOUND);
        }

        if (id != customUserDetails.getSiteUser().getId()) {
            throw new GlobalException(GlobalErrorCode.UNAUTHORIZATION_USER);
        }
    }

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

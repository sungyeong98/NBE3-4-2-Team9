package com.backend.domain.jobskill.dto;

import lombok.Builder;

/**
 * JobSkillResponse
 * <p>JobSkill 응답용 객체 입니다.</p>
 *
 * @param name DisplayName
 * @param code JobSkillCode
 * @author Kim Dong O
 */
@Builder
public record JobSkillResponse(String name, Integer code) {

}

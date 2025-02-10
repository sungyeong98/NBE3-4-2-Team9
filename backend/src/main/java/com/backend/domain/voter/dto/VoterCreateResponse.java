package com.backend.domain.voter.dto;

import com.backend.domain.voter.domain.VoterType;
import lombok.Builder;

/**
 * VoterCreateResponse
 * <p>Voter 생성시 응답 객체 입니다.</p></p>
 * @param targetId 관심 타겟 ID
 * @param voterType {@link VoterType} 관심 타입
 */
@Builder
public record VoterCreateResponse(Long targetId, VoterType voterType) {

}

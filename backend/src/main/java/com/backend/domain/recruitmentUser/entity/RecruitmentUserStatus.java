package com.backend.domain.recruitmentUser.entity;

import com.backend.domain.voter.domain.VoterType;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.stream.Stream;

public enum RecruitmentUserStatus {
    APPLIED,   // 지원 완료
    ACCEPTED,  // 수락됨
    REJECTED,  // 거절됨
    NOT_APPLIED; // 지원하지 않음

    @JsonCreator
	public static RecruitmentUserStatus from(String param) {
		return Stream.of(RecruitmentUserStatus.values())
			.filter(recruitmentUserStatus -> recruitmentUserStatus.toString().equalsIgnoreCase(param))
			.findFirst()
			.orElse(null);
	}
}

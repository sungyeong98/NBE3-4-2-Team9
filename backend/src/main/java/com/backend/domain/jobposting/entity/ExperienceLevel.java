package com.backend.domain.jobposting.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * ExperienceLevel
 * <p>지원자 경력 조건을 관리하는 객체 입니다.</p>
 *
 * @author Kim Dong O
 */
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
public class ExperienceLevel {

	@Column(name = "experience_level_code")
	private Integer code; //code

	@Column(name = "experience_level_min")
	private Integer min; //경력 최소 값

	@Column(name = "experience_level_max")
	private Integer max; //경력 최대 값

	@Column(name = "experience_level_name")
	private String name; //DisplayName
}

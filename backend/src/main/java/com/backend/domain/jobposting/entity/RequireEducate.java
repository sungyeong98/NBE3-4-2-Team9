package com.backend.domain.jobposting.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * RequireEducate
 * <p>지원자 학력 조건을 관리하는 객체 입니다.</p>
 *
 * @author Kim Dong O
 */
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
public class RequireEducate {

	@Column(name = "require_educate_code")
	private String code; //code

	@Column(name = "require_educate_name")
	private String name; //DisplayName
}

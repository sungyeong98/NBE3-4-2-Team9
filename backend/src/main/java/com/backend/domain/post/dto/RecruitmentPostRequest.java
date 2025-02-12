package com.backend.domain.post.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
@ToString
public class RecruitmentPostRequest extends FreePostRequest {

	@NotNull
	private Long jobPostingId;

	@Min(value = 1, message = "모집 인원은 최소 1명 이상이어야 합니다.")
    private Integer numOfApplicants;


}

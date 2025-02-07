package com.backend.domain.voter.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.stream.Stream;

public enum VoterType {
	JOB_POSTING, POST;

	@JsonCreator
	public static VoterType from(String param) {
		return Stream.of(VoterType.values())
			.filter(likeType -> likeType.toString().equalsIgnoreCase(param))
			.findFirst()
			.orElse(null);
	}
}

package com.backend.global.response;

import java.time.ZonedDateTime;
import lombok.AccessLevel;
import lombok.Builder;

/**
 * GenericResponse
 * <p>공통 응답 객체 입니다.</p>
 * @author Kim Dong O
 */
public class GenericResponse<T> {
	private final ZonedDateTime timestamp;
	private final boolean isSuccess;
	private final String code;
	private final T data;
	private final String message;

	@Builder(access = AccessLevel.PRIVATE)
	private GenericResponse(boolean isSuccess, String code, T data, String message) {
		this.timestamp = ZonedDateTime.now();
		this.isSuccess = isSuccess;
		this.code = code;
		this.data = data;
		this.message = message;
	}


}

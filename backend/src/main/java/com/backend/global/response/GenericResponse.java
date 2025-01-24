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

/**
	 * 요청이 성공하고 code, data, message가 있을 때
	 * @param code 응답 상태 코드
	 * @param data 응답 데이터
	 * @param message 응답 메세지
	 * @return {@link GenericResponse<T>}
	 */
	public static <T>GenericResponse<T> of(String code, T data, String message) {
		return GenericResponse.<T>builder()
			.isSuccess(true)
			.code(code)
			.data(data)
			.message(message)
			.build();
	}

	/**
	 * 요청이 성공하고 code, data만 있을 때
	 * @param code 응답 상태 코드
	 * @param data 응답 데이터
	 * @return {@link GenericResponse<T>}
	 */
	public static <T>GenericResponse<T> of(String code, T data) {
		return GenericResponse.<T>builder()
			.isSuccess(true)
			.code(code)
			.data(data)
			.build();
	}
}

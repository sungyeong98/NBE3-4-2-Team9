package com.backend.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * GlobalErrorCode
 * <p>예외 발생시 사용할 ErrorCode 입니다. <br>
 * 응답시 상태 코드, 커스텀 코드, 메세지를 정의합니다. <br>
 * 커스텀 코드는 각 도메인 별로 100 단위로 코드를 정의해주시면 됩니다. <br>
 * ex) Board - 100 -> 커스텀 코드 1001, 1002 순으로 정의하시면 됩니다.</p>
 */
@Getter
@RequiredArgsConstructor
public enum GlobalErrorCode {
	//공통 서버 에러 코드 500
	NOT_VALID(HttpStatus.BAD_REQUEST, "5001", "요청하신 유효성 검증에 실패하였습니다.");

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;
}

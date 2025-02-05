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
 * @author Kim Dong O
 */
@Getter
@RequiredArgsConstructor
public enum GlobalErrorCode {

	// 유저 도메인 에러 코드
	USER_NOT_FOUND(HttpStatus.NOT_FOUND, 4001, "유저가 존재하지 않습니다."),
	UNAUTHENTICATION_USER(HttpStatus.UNAUTHORIZED, 4002, "인증되지 않은 사용자입니다."),
	UNAUTHORIZATION_USER(HttpStatus.FORBIDDEN, 4003, "접근 권한이 없는 유저입니다."),
	BAD_REQUEST(HttpStatus.BAD_REQUEST, 4004, "잘못된 접근입니다."),
	KAKAO_LOGIN_FAIL(HttpStatus.BAD_REQUEST, 4005, "카카오 로그인에 실패하였습니다."),
	INVALID_JOB_SKILL(HttpStatus.BAD_REQUEST, 4006, "유효하지 않은 직무 기술입니다."),

    // 게시글 도메인 에러 코드
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, 1001, "카테고리가 존재하지 않습니다."),
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, 1002, "게시글이 존재하지 않습니다."),

	// 카테고리 도메인 에러 코드
	DUPLICATED_CATEGORY_NAME(HttpStatus.BAD_REQUEST, 3001, "카테고리 이름이 이미 존재합니다."),
	INVALID_CATEGORY_NAME(HttpStatus.BAD_REQUEST, 3002, "카테고리 이름이 유효하지 않습니다."),
	INVALID_CATEGORY_ID(HttpStatus.BAD_REQUEST, 3003, "카테고리 ID가 유효하지 않습니다."),
	ID_MISMATCH(HttpStatus.BAD_REQUEST, 3004, "카테고리 ID가 일치하지 않습니다."),

	// 채용공고 서비스 에러 코드
	NO_DATA_RECEIVED(HttpStatus.NO_CONTENT, 5003, "받아온 데이터가 없습니다."),
	API_REQUEST_FAILED(HttpStatus.BAD_GATEWAY, 5001, "외부 API 요청에 실패했습니다."),
	JSON_PARSING_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, 5002, "응답 데이터를 처리하는중 오류가 발생했습니다."),
	DATABASE_SAVE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, 5005, "데이터 저장중 오류가 발생했습니다."),

	//공통 서버 에러 코드 500
	NOT_VALID(HttpStatus.BAD_REQUEST, 5001, "요청하신 유효성 검증에 실패하였습니다."),
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 5002, "서버 내부 오류가 발생하였습니다.");

	private final HttpStatus httpStatus;
	private final int code;
	private final String message;
}

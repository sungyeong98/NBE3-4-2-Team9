package com.backend.global.advice;

import com.backend.global.exception.GlobalException;
import com.backend.global.response.GenericResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * GlobalControllerAdvice
 * <p>공통 예외 처리를 담당하는 클래스 입니다.</p>
 * @author Kim Dong O
 */
@RestControllerAdvice
@Slf4j
public class GlobalControllerAdvice {

	/**
	 * GlobalException 처리 핸들러 입니다.
	 * @param globalException {@link GlobalException}
	 * @return {@link ResponseEntity<GenericResponse>}
	 */
	@ExceptionHandler(GlobalException.class)
	public ResponseEntity<GenericResponse<Void>> handler(GlobalException globalException) {
		log.error("GlobalException: ", globalException);

		GenericResponse<Void> genericResponse = GenericResponse.of(false,
			globalException.getMessage());

		return ResponseEntity.status(globalException.getStatus().value()).body(genericResponse);
	}
}

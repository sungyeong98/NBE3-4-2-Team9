package com.backend.domain.post.util;

import jakarta.validation.constraints.Min;

/**
 * 게시글 검색 조건 객체 입니다.
 *
 * @param categoryId 카테고리 ID
 * @param kw         검색할 키워드
 * @param sort       정렬 기준 필드
 * @param order      정렬 조건
 * @param pageNum    페이지 번호
 * @param pageSize   페이지 사이즈
 * @author Kim Dong O
 */
public record PostSearchCondition(
	Long categoryId,
	String kw,
	String sort,
	String order,
	@Min(value = 0, message = "음수는 입력할 수 없습니다.")
	Integer pageNum,
	@Min(value = 1, message = "1 이상의 값을 입력해 주세요")
	Integer pageSize
) {

}

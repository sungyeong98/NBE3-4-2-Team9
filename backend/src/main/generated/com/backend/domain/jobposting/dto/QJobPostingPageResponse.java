package com.backend.domain.jobposting.dto;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.ConstructorExpression;
import javax.annotation.processing.Generated;

/**
 * com.backend.domain.jobposting.dto.QJobPostingPageResponse is a Querydsl Projection type for JobPostingPageResponse
 */
@Generated("com.querydsl.codegen.DefaultProjectionSerializer")
public class QJobPostingPageResponse extends ConstructorExpression<JobPostingPageResponse> {

    private static final long serialVersionUID = 241929930L;

    public QJobPostingPageResponse(com.querydsl.core.types.Expression<Long> id, com.querydsl.core.types.Expression<String> subject, com.querydsl.core.types.Expression<java.time.ZonedDateTime> openDate, com.querydsl.core.types.Expression<java.time.ZonedDateTime> closeDate, com.querydsl.core.types.Expression<? extends com.backend.domain.jobposting.entity.ExperienceLevel> experienceLevel, com.querydsl.core.types.Expression<? extends com.backend.domain.jobposting.entity.RequireEducate> requireEducate, com.querydsl.core.types.Expression<com.backend.domain.jobposting.entity.JobPostingStatus> jobPostingStatus, com.querydsl.core.types.Expression<? extends com.backend.domain.jobposting.entity.Salary> salary, com.querydsl.core.types.Expression<Long> applyCnt) {
        super(JobPostingPageResponse.class, new Class<?>[]{long.class, String.class, java.time.ZonedDateTime.class, java.time.ZonedDateTime.class, com.backend.domain.jobposting.entity.ExperienceLevel.class, com.backend.domain.jobposting.entity.RequireEducate.class, com.backend.domain.jobposting.entity.JobPostingStatus.class, com.backend.domain.jobposting.entity.Salary.class, long.class}, id, subject, openDate, closeDate, experienceLevel, requireEducate, jobPostingStatus, salary, applyCnt);
    }

}


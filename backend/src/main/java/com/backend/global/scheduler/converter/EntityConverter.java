package com.backend.global.scheduler.converter;

import com.backend.domain.jobposting.entity.ExperienceLevel;
import com.backend.domain.jobposting.entity.JobPosting;
import com.backend.domain.jobposting.entity.RequireEducate;
import com.backend.global.scheduler.apiresponse.Job;
import com.backend.global.scheduler.apiresponse.position.ExperienceLevelDto;
import com.backend.global.scheduler.apiresponse.position.RequireEducateDto;

public class EntityConverter {

    public static JobPosting jobToJobPosting(Job job) {

        job.setJobPostingStatus();

        return JobPosting.builder()
            .subject(job.getPositionDto().getTitle())
            .url(job.getUrl())
            .postDate(job.getPostDate())
            .openDate(job.getOpenDate())
            .closeDate(job.getCloseDate())
            .companyName(job.getCompanyDto().getCompanyDetailDto().getName())
            .companyLink(job.getCompanyDto().getCompanyDetailDto().getHref())
            .experienceLevel(job.getPositionDto().getExperienceLevel().toEntity())
            .requireEducate(job.getPositionDto().getRequireEducate().toEntity())
            .jobPostingStatus(job.getJobPostingStatus())
            .salary(job.getSalaryDto().toEntity())
            .build();

    }

    public static RequireEducate dtoToRequireEducate(RequireEducateDto dto) {
        return RequireEducate.builder()
            .code(dto.getCode())
            .name(dto.getName())
            .build();
    }

    public static ExperienceLevel dtoToExperienceLevel(ExperienceLevelDto dto) {

        return ExperienceLevel.builder()
            .code(dto.getCode())
            .min(dto.getMin())
            .max(dto.getMax())
            .name(dto.getName())
            .build();
    }



}

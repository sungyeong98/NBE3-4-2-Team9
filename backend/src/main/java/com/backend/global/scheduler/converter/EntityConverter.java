package com.backend.global.scheduler.converter;

import com.backend.domain.jobposting.entity.ExperienceLevel;
import com.backend.domain.jobposting.entity.JobPosting;
import com.backend.domain.jobposting.entity.RequireEducate;
import com.backend.domain.jobposting.entity.Salary;
import com.backend.domain.jobskill.entity.JobSkill;
import com.backend.global.scheduler.apiresponse.Job;
import com.backend.global.scheduler.apiresponse.position.ExperienceLevelDto;
import com.backend.global.scheduler.apiresponse.position.JobCodeDto;
import com.backend.global.scheduler.apiresponse.position.RequireEducateDto;
import com.backend.global.scheduler.apiresponse.salary.SalaryDto;

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
            .code(Integer.parseInt(dto.getCode()))
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

    public static Salary dtoToSalary(SalaryDto dto) {

        return Salary.builder()
            .code(Integer.parseInt(dto.getCode()))
            .name(dto.getName())
            .build();
    }

    public static JobSkill dtoToJobSkill(JobCodeDto dto) {
        return JobSkill.builder()
            .code(Integer.parseInt(dto.getCode()))
            .name(dto.getName())
            .build();
    }



}

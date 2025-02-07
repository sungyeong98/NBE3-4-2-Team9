package com.backend.global.scheduler.apiresponse;

import com.backend.domain.jobposting.entity.JobPosting;
import com.backend.domain.jobposting.entity.JobPostingStatus;
import com.backend.domain.jobskill.entity.JobSkill;
import com.backend.global.scheduler.apiresponse.company.CompanyDto;
import com.backend.global.scheduler.apiresponse.position.PositionDto;
import com.backend.global.scheduler.apiresponse.salary.SalaryDto;
import com.backend.global.scheduler.converter.EntityConverter;
import com.backend.global.scheduler.timeconverter.UnixTimestampDeserializer;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.time.ZonedDateTime;
import lombok.Data;

@Data
public class Job {

    @JsonProperty("url")
    private String url; //url

    @JsonProperty("active")
    private Integer active;

    private JobPostingStatus jobPostingStatus;

    @JsonProperty("company")
    private CompanyDto companyDto; //회사 이름, 링크

    @JsonProperty("position")
    private PositionDto positionDto;

    @JsonProperty("salary")
    private SalaryDto salaryDto;

    @JsonProperty("id")
    private String id;

    /**
     * JSON 데이터를 Java 객체로 변환할 때 사용되는 설정입니다.
     *
     * - @JsonProperty("posting-timestamp")
     *  -> JSON 필드명이 "posting-timestamp"일 경우, 해당 값을 `postDate` 필드에 매핑합니다.
     * - @JsonDeserialize(using = UnixTimestampDeserializer.class)
     *  -> "posting-timestamp" 값을 'UnixTimestampDeserializer' 클래스를 사용하여 'ZonedDateTime' 타입으로 변환.
     */
    @JsonProperty("posting-timestamp")
    @JsonDeserialize(using = UnixTimestampDeserializer.class)
    private ZonedDateTime postDate; //작성 날짜

    @JsonProperty("opening-timestamp")
    @JsonDeserialize(using = UnixTimestampDeserializer.class)
    private ZonedDateTime openDate; //공개 날짜

    @JsonProperty("expiration-timestamp")
    @JsonDeserialize(using = UnixTimestampDeserializer.class)
    private ZonedDateTime closeDate; //마감 날짜


    public void setJobPostingStatus() {
        if (active == 0) {
            this.setJobPostingStatus(JobPostingStatus.END);
        } else {
            this.setJobPostingStatus(JobPostingStatus.ACTIVE);
        }
    }

    public JobPosting toEntity() {
        return EntityConverter.jobToJobPosting(this);
    }

    public JobSkill toJobSkill() {
        return EntityConverter.dtoToJobSkill(positionDto.getJobCode());
    }


}

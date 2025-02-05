package com.backend.global.scheduler.apiresponse.salary;

import com.backend.domain.jobposting.entity.Salary;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SalaryDto {

    @JsonProperty("code")
    private String code;

    @JsonProperty("name")
    private String name;

    public Salary toEntity() {
        return Salary.builder()
            .code(this.code)
            .name(this.name)
            .build();
    }

}

package com.backend.global.scheduler.apiresponse.salary;

import com.backend.domain.jobposting.entity.Salary;
import com.backend.global.scheduler.converter.EntityConverter;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SalaryDto {

    @JsonProperty("code")
    private String code;

    @JsonProperty("name")
    private String name;

    public Salary toEntity() {
        return EntityConverter.dtoToSalary(this);
    }

}

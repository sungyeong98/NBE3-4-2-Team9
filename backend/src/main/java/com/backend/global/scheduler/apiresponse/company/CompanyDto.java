package com.backend.global.scheduler.apiresponse.company;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CompanyDto {

    @JsonProperty("detail")
    private CompanyDetailDto companyDetailDto;

}

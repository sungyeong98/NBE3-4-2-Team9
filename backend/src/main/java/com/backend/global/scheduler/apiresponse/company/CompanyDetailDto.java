package com.backend.global.scheduler.apiresponse.company;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CompanyDetailDto {

    @JsonProperty("href")
    private String href;
    @JsonProperty("name")
    private String name;
}

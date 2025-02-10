package com.backend.global.scheduler.apiresponse.position;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class JobCodeDto {

    @JsonProperty("code")
    private String code;

    @JsonProperty("name")
    private String name;


}

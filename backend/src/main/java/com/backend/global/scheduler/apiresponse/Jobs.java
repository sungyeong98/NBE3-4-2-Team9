package com.backend.global.scheduler.apiresponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Jobs {

    @JsonProperty("jobs")
    private JobsDetail jobsDetail;


}

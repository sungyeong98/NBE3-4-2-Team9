package com.backend.global.scheduler.apiresponse.position;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PositionDto {

    @JsonProperty("title")
    private String title;

    @JsonProperty("experience-level")
    private ExperienceLevelDto experienceLevel;

    @JsonProperty("required-education-level")
    private RequireEducateDto requireEducate;


}

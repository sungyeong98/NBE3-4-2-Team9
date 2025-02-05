package com.backend.global.scheduler.apiresponse.position;

import com.backend.domain.jobposting.entity.ExperienceLevel;
import com.backend.global.scheduler.converter.EntityConverter;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ExperienceLevelDto {

    @JsonProperty("code")
    private Integer code;

    @JsonProperty("min")
    private Integer min;

    @JsonProperty("max")
    private Integer max;

    @JsonProperty("name")
    private String name;

    public ExperienceLevel toEntity() {
        return EntityConverter.dtoToExperienceLevel(this);
    }


}

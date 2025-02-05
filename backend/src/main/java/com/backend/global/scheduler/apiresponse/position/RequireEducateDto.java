package com.backend.global.scheduler.apiresponse.position;

import com.backend.domain.jobposting.entity.RequireEducate;
import com.backend.global.scheduler.converter.EntityConverter;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RequireEducateDto {

    @JsonProperty("code")
    private Integer code;

    @JsonProperty("name")
    private String name;

    public RequireEducate toEntity() {
        return EntityConverter.dtoToRequireEducate(this);
    }

}

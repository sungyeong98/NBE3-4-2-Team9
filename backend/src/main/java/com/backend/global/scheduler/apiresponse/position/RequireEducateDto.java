package com.backend.global.scheduler.apiresponse.position;

import com.backend.domain.jobposting.entity.RequireEducate;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RequireEducateDto {

    @JsonProperty("code")
    private String code;

    @JsonProperty("name")
    private String name;

    public RequireEducate toEntity() {
        return RequireEducate.builder()
            .code(this.code)
            .name(this.name)
            .build();
    }

}

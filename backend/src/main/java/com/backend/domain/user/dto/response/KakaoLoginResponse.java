package com.backend.domain.user.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class KakaoLoginResponse {

    private long id;
    private String email;
    private String name;
    private String profileImg;

}

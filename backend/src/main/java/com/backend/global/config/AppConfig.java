package com.backend.global.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    // 프론트작업을 위한 설정
    public static String getSiteFrontUrl() {
        return "http://localhost:3000";
    }

}

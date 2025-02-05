package com.backend.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    // 프론트작업을 위한 설정
    public static String getSiteFrontUrl() {
        return "http://localhost:3000";
    }


    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}

package com.backend.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.retry.support.RetryTemplateBuilder;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    // 프론트작업을 위한 설정
    public static String getSiteFrontUrl() {
        return "http://localhost:3000";
    }

    /**
     *  SchedulerService 클래스의 의존성 주입을 위한 메소드
     *
     *  - RestTemplate : 외부 웹 서비스와의 통신이 필요할 때 사용.
     *  - SchedulerService 클래스에서 사용목표: 주어진 URI로 채용공고 api 서버에 GET 요청을 보내, 응답 데이터를 받아오는 역할수행
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    /**
     * SchedulerService 클래스의 의존성 주입을 위한 메소드
     *
     * - 최대 3번까지 재시도
     * - 재시도 간격을 3초로 설정
     * - RestClientException 즉 네트워크 문제, 서버응답 시간 초과 같은 문제들이 발생할 경우에만 재시도
     */
    @Bean
    public RetryTemplate retryTemplate() {
        return new RetryTemplateBuilder()
            .maxAttempts(3) // 최대 3번 재시도
            .fixedBackoff(3000) // 각 재시도 사이의 대기 시간을 3초로 설정
            .retryOn(RestClientException.class) // RestClientException 발생 시에만 재시도 수행
            .build();
    }

}

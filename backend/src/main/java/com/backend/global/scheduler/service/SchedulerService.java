package com.backend.global.scheduler.service;

import com.backend.domain.jobposting.entity.JobPosting;
import com.backend.domain.jobposting.repository.JobPostingRepository;
import com.backend.global.exception.GlobalErrorCode;
import com.backend.global.exception.GlobalException;
import com.backend.global.scheduler.apiresponse.Job;
import com.backend.global.scheduler.apiresponse.Jobs;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.retry.support.RetryTemplateBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Service
@RequiredArgsConstructor
public class SchedulerService {

    private final JobPostingRepository jobPostingRepository;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;
    private final RetryTemplate retryTemplate;

    private final String API_URL = "https://oapi.saramin.co.kr/job-search";
    private static final int MAX_RESULTS = 1000;

    @Value("${API.KEY}")
    private String apiKey;

    @Bean
    public RetryTemplate retryTemplate() {
        return new RetryTemplateBuilder()
            .maxAttempts(3)
            .fixedBackoff(3000)
            .retryOn(RestClientException.class)
            .build();
    }


    @Scheduled(cron = "0 0 22,23 * * ?", zone = "Asia/Seoul")
    public void savePublicData() {

        int pageNumber = 0;
        int totalCount = 0;

        while (totalCount < MAX_RESULTS) {

            List<JobPosting> jobList = fetchJobPostings(pageNumber);
            List<JobPosting> newJobs = filterNewJobs(jobList);

            saveNewJobs(newJobs);

            totalCount += newJobs.size();
            pageNumber ++;
        }

    }

    private List<JobPosting> fetchJobPostings(int start) {
        return retryTemplate.execute(context -> {
            URI uri = UriComponentsBuilder.fromHttpUrl(API_URL)
                .queryParam("access-key", apiKey)
                .queryParam("published", getPublishedDate())
                .queryParam("job_mid_cd", "2")
                .queryParam("start", start)
                .build()
                .encode()
                .toUri();

            try {
                String jsonResponse = restTemplate.getForObject(uri, String.class);
                log.info("API 응답: {}", jsonResponse);

                if (jsonResponse == null || jsonResponse.isEmpty()) {
                    log.error(GlobalErrorCode.NO_DATA_RECEIVED.getMessage());
                    throw new GlobalException(GlobalErrorCode.NO_DATA_RECEIVED); //
                }

                Jobs dataResponse = objectMapper.readValue(jsonResponse, Jobs.class);
                return dataResponse.getJobsDetail().getJobList().stream()
                    .map(Job :: toEntity)
                    .toList();

            } catch (RestClientException e) {
                log.error("API 요청 실패: {}", uri, e);
                throw new GlobalException(GlobalErrorCode.API_REQUEST_FAILED); //
            } catch (JsonProcessingException e) {
                log.error("JSON 파싱 실패", e);
                throw new GlobalException(GlobalErrorCode.JSON_PARSING_FAILED); //
            }
        });

    }

    private List<JobPosting> filterNewJobs(List<JobPosting> jobList) {
        Set<Long> existingJobIds = new HashSet<>(jobPostingRepository.findAllIds());

        return jobList.stream()
            .filter(job -> !existingJobIds.contains(job.getId()))
            .toList();
    }

    private String getPublishedDate() {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return today.format(formatter);
    }

    @Transactional
    private void saveNewJobs(List<JobPosting> newJobs) {
        try {
            jobPostingRepository.saveAll(newJobs);
            log.info("총 {}개의 공고를 저장했습니다.", newJobs.size());
        } catch (Exception e) {
            log.error(GlobalErrorCode.DATABASE_SAVE_FAILED.getMessage(), e);
            throw new GlobalException(GlobalErrorCode.DATABASE_SAVE_FAILED);
        }
    }




}

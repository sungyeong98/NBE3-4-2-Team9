package com.backend.global.scheduler.service;

import com.backend.domain.jobposting.entity.JobPosting;
import com.backend.domain.jobposting.repository.JobPostingRepository;
import com.backend.global.scheduler.apiresponse.Job;
import com.backend.global.scheduler.apiresponse.Jobs;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

    private final String API_URL = "https://oapi.saramin.co.kr/job-search";
    private static final int MAX_RESULTS = 1000;
    private static final int PAGE_SIZE = 100;

    @Value("${API.KEY}")
    private String apiKey;


    @Scheduled(cron = "0 0 3 * * ?", zone = "Asia/Seoul")
    @Transactional
    public void savePublicData() {

        // 공고 데이터를 저장할 리스트
        List<JobPosting> allJobPostings = new ArrayList<>();

        int pageNumber = 0;
        int totalCount = 0;

        while (totalCount < MAX_RESULTS) {

            List<JobPosting> jobList = fetchJobPostings(pageNumber);

            if (jobList.isEmpty()) {
                log.info("더 이상 가져올 데이터가 없습니다.");
                break;
            }

            List<JobPosting> newJobs = filterNewJobs(jobList);
            jobPostingRepository.saveAll(newJobs);
            log.info("총 {}개의 공고를 저장했습니다.", newJobs.size());

            totalCount += newJobs.size();
            pageNumber ++;
        }



    }

    private List<JobPosting> fetchJobPostings(int start) {
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

            Jobs dataResponse = objectMapper.readValue(jsonResponse, Jobs.class);
            return dataResponse.getJobsDetail().getJobList().stream()
                .map(Job :: toEntity)
                .toList();

        } catch (RestClientException e) {
            log.error("API 요청 실패: {}", uri, e);
        } catch (JsonProcessingException e) {
            log.error("JSON 파싱 실패", e);
        }

        return Collections.emptyList();
    }


    private List<JobPosting> filterNewJobs(List<JobPosting> jobList) {
        return jobList.stream()
            .filter(job -> !jobPostingRepository.existsById(job.getId()))
            .toList();
    }

    private String getPublishedDate() {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return today.format(formatter);
    }



}

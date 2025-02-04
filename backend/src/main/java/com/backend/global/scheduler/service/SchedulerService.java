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

//    private final String API_URL = "https://oapi.saramin.co.kr/job-search?access-key=YpYSmykXUtWhZkDrVkHdPOnT0VusIHP5LNwOb1WN87NfKD0y8BC&job_mid_cd=2";
//    private final RestTemplate restTemplate = new RestTemplate();

    private final JobPostingRepository jobPostingRepository;
    private final ObjectMapper objectMapper;

    private final String apiUrl = "https://oapi.saramin.co.kr/job-search";

    @Value("${API.KEY}")
    private String apiKey;

    private static final int MAX_RESULTS = 1000;

    @Scheduled(cron = "0 0 3 * * ?", zone = "Asia/Seoul")
    @Transactional
    public void savePublicData() {

        // 공고 데이터를 저장할 리스트
        List<JobPosting> allJobPostings = new ArrayList<>();

        int pageNumber = 0;
        int totalCount = 0;

        while (totalCount < MAX_RESULTS) {

            URI uri = UriComponentsBuilder.fromHttpUrl(apiUrl)
                .queryParam("access-key", apiKey)
                .queryParam("published", getPublishedDate())
                .queryParam("job_mid_cd", "2")
                .queryParam("start", pageNumber)
                .build()
                .encode()
                .toUri();

            String jsonResponse;

            try {
                jsonResponse = new RestTemplate().getForObject(uri, String.class);
                log.info("JSON 응답 : " + jsonResponse);
            } catch (RestClientException e) {
                log.error("API 요청 실패: {}", uri, e);
                return;
            }

            try {
                Jobs dataResponse = objectMapper.readValue(jsonResponse, Jobs.class);
                List<JobPosting> jobList = dataResponse.getJobsDetail().getJobList().stream()
                    .map(Job:: toEntity)
                    .toList();

                // 데이터가 없으면 종료
                if (jobList.isEmpty()) {
                    break;
                }

                allJobPostings.addAll(jobList);
                totalCount += jobList.size();


                // 페이지 번호 증가
                pageNumber++;

            } catch (JsonProcessingException e) {
                log.error("JSON 파싱 실패 : {}", jsonResponse, e);
            }

            if (!allJobPostings.isEmpty()) {
                jobPostingRepository.saveAll(allJobPostings);
                log.info("총 {}개의 공고를 저장했습니다.", allJobPostings.size());
            } else {
                log.info("저장할 공고가 없습니다.");
            }

        }



    }

    private String getPublishedDate() {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return today.format(formatter);
    }


//    @Scheduled(cron = "0 0 3 * * ?", zone = "Asia/Seoul")
//    public void savePublicData() {
//
//        Jobs response = restTemplate.getForObject(API_URL, Jobs.class);
//
//        if (response == null || response.getJobsDetail() == null || response.getJobsDetail().getJobList().isEmpty()) {
//            throw new GlobalException(GlobalErrorCode.OPEN_API_DATA_NOT_FOUND);
//        }
//
//        List<JobPosting> publicDataList = response.getJobsDetail().getJobList().stream()
//            .map(Job::toEntity)
//            .toList();
//
//        jobPostingRepository.saveAll(publicDataList);
//    }


}

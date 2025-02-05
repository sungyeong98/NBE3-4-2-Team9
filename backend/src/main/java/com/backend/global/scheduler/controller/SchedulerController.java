package com.backend.global.scheduler.controller;

import com.backend.global.scheduler.service.SchedulerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/scheduler")
@RequiredArgsConstructor
public class SchedulerController {

    private final SchedulerService schedulerService;

    @PostMapping("/run")
    public ResponseEntity<String> runScheduler() {
        schedulerService.triggerSavePublicDataManually();
        return ResponseEntity.ok("스케줄러 실행 완료.");
    }

}

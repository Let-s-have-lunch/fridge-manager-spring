package com.teamproject.fridgemanagerspring.controller;

import com.teamproject.fridgemanagerspring.dto.statistics.response.StatisticsResponse;
import com.teamproject.fridgemanagerspring.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<Map<String, Object>> getUserStatistics(
            @AuthenticationPrincipal Long currentUserId,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {
        try {
            LocalDate today = LocalDate.now();
            int targetYear = (year != null) ? year : today.getYear();
            int targetMonth = (month != null) ? month : today.getMonthValue();

            StatisticsResponse result = statisticsService.getUserStatistics(currentUserId, targetYear, targetMonth);

            return ResponseEntity.ok(Map.<String, Object>of(
                    "message", "냉장고 통계 조회 성공",
                    "data", result
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.<String, Object>of("message", "서버 오류가 발생했습니다."));
        }
    }
}

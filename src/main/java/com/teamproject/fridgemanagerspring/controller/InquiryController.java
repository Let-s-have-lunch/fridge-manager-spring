package com.example.fridgemanagerspring.controller;

import com.example.fridgemanagerspring.domain.inquiry.Inquiry;
import com.example.fridgemanagerspring.dto.common.PaginationResponse;
import com.example.fridgemanagerspring.dto.inquiry.request.InquiryRequest;
import com.example.fridgemanagerspring.dto.inquiry.response.InquiryResponse;
import com.example.fridgemanagerspring.service.InquiryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/inquiries")
@RequiredArgsConstructor
public class InquiryController {

    private final InquiryService inquiryService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getInquiryList(
            @AuthenticationPrincipal Long currentUserId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Page<Inquiry> inquiryPage = inquiryService.getInquiryList(currentUserId, page, size);

            // 1. Entity List -> DTO List 로 변환 (Java 16+ toList 활용)
            List<InquiryResponse> list = inquiryPage.getContent().stream()
                    .map(InquiryResponse::from)
                    .toList();

            // 2. PaginationResponse 에 담기
            PaginationResponse<InquiryResponse> paginationResult = PaginationResponse.of(
                    page,
                    size,
                    inquiryPage.getTotalElements(),
                    list
            );

            // 3. Map.of 를 활용한 응답 (경고를 피하기 위해 명시적 제네릭 제거)
            return ResponseEntity.ok(Map.of(
                    "message", "문의 목록 조회 성공",
                    "data", paginationResult
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "문의 목록 조회 중 서버 오류가 발생되었습니다."));
        }
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{inquiryId}")
    public ResponseEntity<Map<String, Object>> getInquiryById(
            @AuthenticationPrincipal Long currentUserId,
            @PathVariable Long inquiryId) {
        try {
            Inquiry inquiry = inquiryService.getInquiryById(inquiryId);

            if (!inquiry.getUser().getId().equals(currentUserId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("message", "해당 문의글을 읽을 권한이 없습니다."));
            }

            return ResponseEntity.ok(Map.of(
                    "message", "문의 조회 성공",
                    "data", InquiryResponse.from(inquiry)
            ));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("NOT_FOUND_INQUIRY")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "문의글을 찾을 수 없습니다."));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "문의글 조회 중 서버 오류가 발생되었습니다."));
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createInquiry(
            @AuthenticationPrincipal Long currentUserId,
            @Valid @RequestBody InquiryRequest request) {
        try {
            Inquiry result = inquiryService.createInquiry(currentUserId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "message", "문의글 등록 성공",
                    "data", InquiryResponse.from(result)
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "문의글 등록 중 서버 오류가 발생되었습니다."));
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/{inquiryId}")
    public ResponseEntity<Map<String, Object>> updateInquiry(
            @AuthenticationPrincipal Long currentUserId,
            @PathVariable Long inquiryId,
            @Valid @RequestBody InquiryRequest request) {
        try {
            Inquiry result = inquiryService.updateInquiry(currentUserId, inquiryId, request);
            return ResponseEntity.ok(Map.of(
                    "message", "문의글 수정 성공",
                    "data", InquiryResponse.from(result)
            ));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("NOT_FOUND_INQUIRY")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "문의글을 찾을 수 없습니다."));
            }
            if (e.getMessage().equals("NOT_YOUR_INQUIRY")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "문의글 수정에 대한 권한이 없습니다."));
            }
            if (e.getMessage().equals("ALREADY_ANSWERED")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "이미 답변이 달린 문의글은 수정할 수 없습니다."));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "문의글 수정 중 서버 오류가 발생되었습니다."));
        }
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{inquiryId}")
    public ResponseEntity<Map<String, Object>> deleteInquiry(
            @AuthenticationPrincipal Long currentUserId,
            @PathVariable Long inquiryId) {
        try {
            inquiryService.deleteInquiry(currentUserId, inquiryId);
            return ResponseEntity.ok(Map.of("message", "문의글 삭제가 성공했습니다."));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("NOT_FOUND_INQUIRY")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "문의글을 찾을 수 없습니다."));
            }
            if (e.getMessage().equals("NOT_YOUR_INQUIRY")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "삭제 권한이 없습니다."));
            }
            if (e.getMessage().equals("ALREADY_ANSWERED")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "답변이 존재하는 문의글은 삭제할 수 없습니다."));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "문의글 삭제 중 서버 오류가 발생되었습니다."));
        }
    }
}
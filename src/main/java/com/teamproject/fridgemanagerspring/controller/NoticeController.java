package com.teamproject.fridgemanagerspring.controller;

import com.teamproject.fridgemanagerspring.domain.common.paginnation.PaginationResponse;
import com.teamproject.fridgemanagerspring.domain.notice.Notice;
import com.teamproject.fridgemanagerspring.dto.notice.request.NoticeRequest;
import com.teamproject.fridgemanagerspring.dto.notice.response.NoticeResponse;
import com.teamproject.fridgemanagerspring.service.NoticeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/notices")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    // 💡 조회(GET)는 누구나 접근 가능 (SecurityConfig에서 해당 경로를 permitAll() 해주셔야 합니다.)
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getNoticeList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<Notice> noticePage = noticeService.getNoticeList(page, size);

            List<NoticeResponse> list = noticePage.getContent().stream()
                    .map(NoticeResponse::from)
                    .toList();

            PaginationResponse<NoticeResponse> paginationData = PaginationResponse.of(
                    page,
                    size,
                    noticePage.getTotalElements(),
                    list
            );

            return ResponseEntity.ok(Map.of(
                    "message", "공지사항 목록을 불러오는데 성공했습니다",
                    "data", paginationData
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "공지사항 목록 조회 중 서버 에러가 발생되었습니다."));
        }
    }

    @GetMapping("/{noticeId}")
    public ResponseEntity<Map<String, Object>> getNoticeById(@PathVariable Long noticeId) {
        try {
            Notice notice = noticeService.getNoticeById(noticeId);
            return ResponseEntity.ok(Map.of(
                    "message", "공지사항 조회 성공",
                    "data", NoticeResponse.from(notice)
            ));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("NOT_FOUND_NOTICE")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "해당 공지사항을 찾을 수 없습니다."));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "공지사항 조회 중 서버 에러가 발생되었습니다."));
        }
    }



    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createNotice(@Valid @RequestBody NoticeRequest request) {
        try {
            Notice result = noticeService.createNotice(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "message", "공지사항 등록 성공",
                    "data", NoticeResponse.from(result)
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "공지사항 등록 중 서버 에러가 발생되었습니다."));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{noticeId}")
    public ResponseEntity<Map<String, Object>> updateNotice(
            @PathVariable Long noticeId,
            @Valid @RequestBody NoticeRequest request) {
        try {
            Notice result = noticeService.updateNotice(noticeId, request);
            return ResponseEntity.ok(Map.of(
                    "message", "공지사항 수정 성공",
                    "data", NoticeResponse.from(result)
            ));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("NOT_FOUND_NOTICE")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "해당 공지사항을 찾을 수 없습니다."));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "공지사항 수정 중 서버 에러가 발생되었습니다."));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{noticeId}")
    public ResponseEntity<Map<String, Object>> deleteNotice(@PathVariable Long noticeId) {
        try {
            noticeService.deleteNotice(noticeId);
            return ResponseEntity.ok(Map.of("message", "공지사항 삭제가 성공했습니다."));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("NOT_FOUND_NOTICE")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "해당 공지사항을 찾을 수 없습니다."));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "공지사항 삭제 중 서버 에러가 발생되었습니다."));
        }
    }
}
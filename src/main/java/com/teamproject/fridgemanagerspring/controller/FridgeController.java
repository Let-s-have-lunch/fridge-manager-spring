package com.teamproject.fridgemanagerspring.controller;

import com.teamproject.fridgemanagerspring.dto.fridge.request.FridgeRequest;
import com.teamproject.fridgemanagerspring.service.FridgeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/fridges")
@RequiredArgsConstructor
public class FridgeController {

    private final FridgeService fridgeService;

    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> getFridgeList(@AuthenticationPrincipal Long currentUserId) {
        try {
            List list = fridgeService.getFridgeList(currentUserId);
            return ResponseEntity.ok(Map.of(
                    "message", "냉장고 목록을 성공적으로 불러왔습니다.",
                    "data", list
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "냉장고 목록 조회 중 서버 에러가 발생했습니다."));
        }
    }

    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createFridge(
            @AuthenticationPrincipal Long currentUserId,
            @Valid @RequestBody FridgeRequest request) {
        try {
            com.teamproject.fridgemanagerspring.domain.fridge.Fridge newFridge = fridgeService.createFridge(currentUserId, request.getName());
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "message", "냉장고가 성공적으로 등록되었습니다.",
                    "data", newFridge
            ));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("ALREADY_EXISTS_NAME")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                        "message", "이미 사용 중인 냉장고 이름입니다. 삭제된 냉장고가 아니라면 다른 이름을 사용해주세요."
                ));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "냉장고 등록 중 서버 에러가 발생했습니다."));
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateFridge(
            @AuthenticationPrincipal Long currentUserId,
            @PathVariable Long id,
            @Valid @RequestBody FridgeRequest request) {
        try {
            com.teamproject.fridgemanagerspring.domain.fridge.Fridge updatedFridge = fridgeService.updateFridge(currentUserId, id, request.getName());
            return ResponseEntity.ok(Map.of(
                    "message", "냉장고 정보가 성공적으로 수정되었습니다.",
                    "data", updatedFridge
            ));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("NOT_FOUND_FRIDGE")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "존재하지 않거나 이미 삭제된 냉장고입니다."));
            }
            if (e.getMessage().equals("ALREADY_EXISTS_NAME")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "변경하시려는 이름이 이미 존재합니다."));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "냉장고 수정 중 서버 에러가 발생했습니다."));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteFridge(
            @AuthenticationPrincipal Long currentUserId,
            @PathVariable Long id) {
        try {
            fridgeService.deleteFridge(currentUserId, id);
            return ResponseEntity.ok(Map.of("message", "냉장고가 성공적으로 삭제되었습니다."));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("NOT_FOUND_FRIDGE")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "존재하지 않거나 이미 삭제된 냉장고입니다."));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "냉장고 삭제 중 서버 에러가 발생했습니다."));
        }
    }
}
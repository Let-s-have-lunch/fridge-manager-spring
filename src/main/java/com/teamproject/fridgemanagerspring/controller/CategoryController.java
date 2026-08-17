package com.teamproject.fridgemanagerspring.controller;

import com.teamproject.fridgemanagerspring.domain.category.Category;
import com.teamproject.fridgemanagerspring.dto.category.request.CategoryRequest;
import com.teamproject.fridgemanagerspring.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getCategoryList(@AuthenticationPrincipal Long currentUserId) {
        try {
            List result = categoryService.getCategoryList(currentUserId);
            return ResponseEntity.ok(Map.of("message", "카테고리 목록 조회 성공", "data", result));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "서버 오류가 발생했습니다."));
        }
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createCategory(
            @AuthenticationPrincipal Long currentUserId,
            @Valid @RequestBody CategoryRequest request) {
        try {
            Category result = categoryService.createCategory(currentUserId, request.getName());
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "카테고리 생성 성공", "data", result));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("DUPLICATED_CATEGORY")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "이미 존재하는 카테고리입니다."));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "서버 오류가 발생했습니다."));
        }
    }

    @PatchMapping("/{categoryId}")
    public ResponseEntity<Map<String, Object>> updateCategory(
            @AuthenticationPrincipal Long currentUserId,
            @PathVariable Long categoryId,
            @Valid @RequestBody CategoryRequest request) {
        try {
            Category result = categoryService.updateCategory(currentUserId, categoryId, request.getName());
            return ResponseEntity.ok(Map.of("message", "카테고리 수정 성공", "data", result));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("CATEGORY_NOT_FOUND"))
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "해당 카테고리를 찾을 수 없습니다."));
            if (e.getMessage().equals("DUPLICATED_CATEGORY"))
                return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "이미 존재하는 카테고리 이름입니다."));
            if (e.getMessage().equals("CANNOT_MODIFY_DEFAULT_CATEGORY"))
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "기본 카테고리는 수정할 수 없습니다."));
            if (e.getMessage().equals("UNAUTHORIZED_ACCESS"))
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "수정 권한이 없습니다."));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "서버 오류가 발생했습니다."));
        }
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Map<String, Object>> deleteCategory(
            @AuthenticationPrincipal Long currentUserId,
            @PathVariable Long categoryId) {
        try {
            categoryService.deleteCategory(currentUserId, categoryId);
            return ResponseEntity.ok(Map.of("message", "카테고리 삭제 성공"));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("CATEGORY_NOT_FOUND"))
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "해당 카테고리를 찾을 수 없습니다."));
            if (e.getMessage().equals("CANNOT_DELETE_DEFAULT_CATEGORY"))
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "기본 카테고리는 삭제할 수 없습니다."));
            if (e.getMessage().equals("UNAUTHORIZED_ACCESS"))
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "삭제 권한이 없습니다."));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "서버 오류가 발생했습니다."));
        }
    }
}
package com.teamproject.fridgemanagerspring.controller;

import com.teamproject.fridgemanagerspring.domain.shoppingList.ShoppingList;
import com.teamproject.fridgemanagerspring.dto.shoppinglist.request.ShoppingListRequest;
import com.teamproject.fridgemanagerspring.dto.shoppinglist.response.ShoppingListResponse;
import com.teamproject.fridgemanagerspring.service.ShoppingListService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/shopping-list")
@RequiredArgsConstructor
public class ShoppingListController {

    private final ShoppingListService shoppingListService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<Map<String, Object>> getItems(
            @AuthenticationPrincipal Long currentUserId,
            // 💡 @DateTimeFormat을 쓰면 YYYY-MM-DD 문자열을 알아서 LocalDate 객체로 변환해 줍니다!
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            List<ShoppingListResponse> list = shoppingListService.getItems(currentUserId, date).stream()
                    .map(ShoppingListResponse::from)
                    .toList();

            return ResponseEntity.ok(Map.of(
                    "message", "하루 단위 장보기 목록을 성공적으로 불러왔습니다.",
                    "data", list
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "장보기 목록 조회 중 서버 에러가 발생했습니다."));
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createItem(
            @AuthenticationPrincipal Long currentUserId,
            @Valid @RequestBody ShoppingListRequest request) {
        try {
            ShoppingList newItem = shoppingListService.createItem(currentUserId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "message", "장보기 메모가 등록되었습니다.",
                    "data", ShoppingListResponse.from(newItem)
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "장보기 메모 등록 중 서버 오류가 발생했습니다."));
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateItem(
            @AuthenticationPrincipal Long currentUserId,
            @PathVariable Long id,
            @Valid @RequestBody ShoppingListRequest request) {
        try {
            ShoppingList updatedItem = shoppingListService.updateItem(currentUserId, id, request);
            return ResponseEntity.ok(Map.of(
                    "message", "장보기 메모가 성공적으로 수정되었습니다.",
                    "data", ShoppingListResponse.from(updatedItem)
            ));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("NOT_FOUND_ITEM")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "장보기 메모를 찾을 수 없습니다."));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "서버 에러가 발생했습니다."));
        }
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteItem(
            @AuthenticationPrincipal Long currentUserId,
            @PathVariable Long id) {
        try {
            shoppingListService.deleteItem(currentUserId, id);
            return ResponseEntity.ok(Map.of("message", "장보기 메모가 성공적으로 삭제되었습니다."));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("NOT_FOUND_ITEM")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "장보기 메모를 찾을 수 없습니다."));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "장보기 메모 삭제 중 오류가 발생했습니다."));
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/{id}/toggle")
    public ResponseEntity<Map<String, Object>> toggleTodo(
            @AuthenticationPrincipal Long currentUserId,
            @PathVariable Long id) {
        try {
            ShoppingList toggledItem = shoppingListService.toggleTodo(currentUserId, id);
            return ResponseEntity.ok(Map.of(
                    "message", "완료 상태가 성공적으로 변경되었습니다.",
                    "data", ShoppingListResponse.from(toggledItem)
            ));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("NOT_FOUND_ITEM")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "존재하지 않는 항목입니다."));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "서버 에러가 발생했습니다."));
        }
    }
}

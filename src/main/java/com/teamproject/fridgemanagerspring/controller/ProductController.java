package com.teamproject.fridgemanagerspring.controller;

import com.teamproject.fridgemanagerspring.domain.product.Product;
import com.teamproject.fridgemanagerspring.dto.product.request.ProductRequest;
import com.teamproject.fridgemanagerspring.dto.product.response.ProductResponse;
import com.teamproject.fridgemanagerspring.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/fridge/{fridgeId}")
    public ResponseEntity<Map<String, Object>> getProductList(
            @AuthenticationPrincipal Long currentUserId,
            @PathVariable Long fridgeId) {
        try {
            List<Product> products = productService.getProductList(currentUserId, fridgeId);
            List<ProductResponse> result = products.stream()
                    .map(ProductResponse::from)
                    .toList();

            // 💡 꺾쇠 제거: Map.of(...) 로 수정
            return ResponseEntity.ok(Map.of("message", "제품 목록 조회 성공", "data", result));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "서버 오류가 발생했습니다."));
        }
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{productId}")
    public ResponseEntity<Map<String, Object>> getProductById(
            @AuthenticationPrincipal Long currentUserId,
            @PathVariable Long productId) {
        try {
            Product product = productService.getProductById(currentUserId, productId);
            return ResponseEntity.ok(Map.of("message", "제품 상세 조회 성공", "data", ProductResponse.from(product)));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("PRODUCT_NOT_FOUND")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "해당 제품을 찾을 수 없습니다."));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "서버 오류가 발생했습니다."));
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/fridge/{fridgeId}")
    public ResponseEntity<Map<String, Object>> createProduct(
            @AuthenticationPrincipal Long currentUserId,
            @PathVariable Long fridgeId,
            @Valid @RequestBody ProductRequest request) {
        try {
            Product newProduct = productService.createProduct(currentUserId, fridgeId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "message", "제품 등록 성공",
                    "data", ProductResponse.from(newProduct)
            ));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("UNAUTHORIZED_ACCESS")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "해당 냉장고에 제품을 등록할 권한이 없습니다."));
            }
            if (e.getMessage().equals("CATEGORY_NOT_FOUND")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "카테고리를 찾을 수 없습니다."));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "서버 오류가 발생했습니다."));
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/{productId}")
    public ResponseEntity<Map<String, Object>> updateProduct(
            @AuthenticationPrincipal Long currentUserId,
            @PathVariable Long productId,
            @Valid @RequestBody ProductRequest request) {
        try {
            Product updatedProduct = productService.updateProduct(currentUserId, productId, request);
            return ResponseEntity.ok(Map.of(
                    "message", "제품 수정 성공",
                    "data", ProductResponse.from(updatedProduct)
            ));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("PRODUCT_NOT_FOUND")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "해당 제품을 찾을 수 없습니다."));
            }
            if (e.getMessage().equals("CATEGORY_NOT_FOUND")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "카테고리를 찾을 수 없습니다."));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "서버 오류가 발생했습니다."));
        }
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{productId}")
    public ResponseEntity<Map<String, Object>> deleteProduct(
            @AuthenticationPrincipal Long currentUserId,
            @PathVariable Long productId) {
        try {
            productService.deleteProduct(currentUserId, productId);
            return ResponseEntity.ok(Map.of("message", "제품 삭제 성공"));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("PRODUCT_NOT_FOUND")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "해당 제품을 찾을 수 없습니다."));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "서버 오류가 발생했습니다."));
        }
    }
}
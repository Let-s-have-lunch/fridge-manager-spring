package com.teamproject.fridgemanagerspring.dto.product.response;

import com.teamproject.fridgemanagerspring.domain.enums.ProductStatus;
import com.teamproject.fridgemanagerspring.domain.enums.StorageType;
import com.teamproject.fridgemanagerspring.domain.enums.Unit;
import com.teamproject.fridgemanagerspring.domain.product.Product;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Getter
@Builder
public class ProductResponse {
    private Long id;
    private String name;
    private String memo;
    private Double quantity;
    private Unit unit;
    private StorageType storageType;
    private Integer price;
    private LocalDate expirationDate;
    private ProductStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long dDay;

    private CategoryDto category;

    // 중첩 클래스로 카테고리의 핵심 정보만 담는 DTO 생성
    @Getter
    @Builder
    public static class CategoryDto {
        private Long id;
        private String name;
        private String icon;
    }

    // Entity를 DTO로 변환하는 정적 팩토리 메서드
    public static ProductResponse from(Product product) {
        // 💡 Java 8+ 문법을 활용한 아주 깔끔한 D-Day 계산 로직
        long calculatedDDay = ChronoUnit.DAYS.between(LocalDate.now(), product.getExpirationDate());

        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .memo(product.getMemo())
                .quantity(product.getQuantity())
                .unit(product.getUnit())
                .storageType(product.getStorageType())
                .price(product.getPrice())
                .expirationDate(product.getExpirationDate())
                .status(product.getStatus())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .dDay(calculatedDDay)
                .category(CategoryDto.builder()
                        .id(product.getCategory().getId())
                        .name(product.getCategory().getName())
                        .icon(product.getCategory().getIcon())
                        .build())
                .build();
    }
}
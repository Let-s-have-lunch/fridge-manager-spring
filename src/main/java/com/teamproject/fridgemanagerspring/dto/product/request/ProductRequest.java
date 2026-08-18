package com.teamproject.fridgemanagerspring.dto.product.request;

import com.teamproject.fridgemanagerspring.domain.enums.ProductStatus;
import com.teamproject.fridgemanagerspring.domain.enums.StorageType;
import com.teamproject.fridgemanagerspring.domain.enums.Unit;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ProductRequest {
    @NotNull(message = "카테고리를 선택해주세요.")
    private Long categoryId;

    @NotBlank(message = "제품 이름을 입력해주세요.")
    private String name;

    @NotNull(message = "보관 방식을 선택해주세요.")
    private StorageType storageType;

    @NotNull(message = "수량을 입력해주세요.")
    private Double quantity;

    @NotNull(message = "단위를 선택해주세요.")
    private Unit unit;

    private Integer price; // 선택 사항

    @NotNull(message = "유통기한을 입력해주세요.")
    private LocalDate expirationDate;

    private ProductStatus status; // 선택 사항 (수정 시 등)

    private String memo; // 선택 사항
}

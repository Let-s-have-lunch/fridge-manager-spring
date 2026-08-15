package com.teamproject.fridgemanagerspring.dto.shoppinglist.response;


import com.teamproject.fridgemanagerspring.domain.shoppingList.ShoppingList;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class ShoppingListResponse {
    private Long id;
    private String memo;
    private LocalDate date;
    private Boolean isChecked;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ShoppingListResponse from(ShoppingList shoppingList) {
        return ShoppingListResponse.builder()
                .id(shoppingList.getId())
                .memo(shoppingList.getMemo())
                .date(shoppingList.getDate())
                .isChecked(shoppingList.getIsChecked())
                .createdAt(shoppingList.getCreatedAt())
                .updatedAt(shoppingList.getUpdatedAt())
                .build();
    }
}
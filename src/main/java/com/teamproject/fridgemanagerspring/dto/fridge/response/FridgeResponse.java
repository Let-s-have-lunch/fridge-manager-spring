package com.teamproject.fridgemanagerspring.dto.fridge.response;

import com.teamproject.fridgemanagerspring.domain.fridge.Fridge;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class FridgeResponse {
    private Long id;
    private String name;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static FridgeResponse from(Fridge fridge) {
        return FridgeResponse.builder()
                .id(fridge.getId())
                .name(fridge.getName())
                .createdAt(fridge.getCreatedAt())
                .updatedAt(fridge.getUpdatedAt())
                .build();
    }
}

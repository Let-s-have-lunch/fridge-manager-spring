package com.teamproject.fridgemanagerspring.dto.shoppinglist.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ShoppingListRequest {
    @NotBlank(message = "메모 내용을 입력해주세요.")
    private String memo;

    @NotNull(message = "날짜를 입력해주세요.")
    private LocalDate date;
}
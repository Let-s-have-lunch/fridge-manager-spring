package com.teamproject.fridgemanagerspring.dto.category.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryRequest {
    @NotBlank(message = "카테고리 이름을 입력해주세요.")
    @Size(max = 30, message = "30자 이내로 입력해주세요.")
    private String name;
}

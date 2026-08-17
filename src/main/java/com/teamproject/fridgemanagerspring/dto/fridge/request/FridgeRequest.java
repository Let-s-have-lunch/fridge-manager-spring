package com.teamproject.fridgemanagerspring.dto.fridge.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FridgeRequest {
    @NotBlank(message = "냉장고 이름은 필수 입력 항목입니다.")
    @Size(max = 10, message = "10자 이내로 입력해주세요.")
    private String name;
}

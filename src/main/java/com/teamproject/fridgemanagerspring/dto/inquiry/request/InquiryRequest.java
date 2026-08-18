package com.teamproject.fridgemanagerspring.dto.inquiry.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InquiryRequest {
    @NotBlank(message = "제목은 필수 입력 항목입니다.")
    private String title;

    @NotBlank(message = "내용은 필수 입력 항목입니다.")
    private String content;
}
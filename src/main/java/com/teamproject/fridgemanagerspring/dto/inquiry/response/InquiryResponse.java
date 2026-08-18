package com.teamproject.fridgemanagerspring.dto.inquiry.response;

import com.teamproject.fridgemanagerspring.domain.inquriy.Inquiry;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class InquiryResponse {
    private Long id;
    private String title;
    private String content;
    private String answer;
    private LocalDateTime answeredAt;
    private LocalDateTime createdAt;
    private UserDto user;

    @Getter
    @Builder
    public static class UserDto {
        private Long id;
        private String nickname;
        private String email;
    }

    public static InquiryResponse from(Inquiry inquiry) {
        return InquiryResponse.builder()
                .id(inquiry.getId())
                .title(inquiry.getTitle())
                .content(inquiry.getContent())
                .answer(inquiry.getAnswer())
                .answeredAt(inquiry.getAnsweredAt())
                .createdAt(inquiry.getCreatedAt())
                .user(UserDto.builder()
                        .id(inquiry.getUser().getId())
                        .nickname(inquiry.getUser().getNickname())
                        .email(inquiry.getUser().getEmail())
                        .build())
                .build();
    }
}
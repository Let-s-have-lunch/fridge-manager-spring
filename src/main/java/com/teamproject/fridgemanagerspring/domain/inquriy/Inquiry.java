package com.example.fridgemanagerspring.domain.inquiry;

import com.teamproject.fridgemanagerspring.domain.common.BaseTimeEntity;
import com.teamproject.fridgemanagerspring.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Inquiry extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(columnDefinition = "TEXT")
    private String answer; // null 허용

    private LocalDateTime answeredAt; // DateTime?

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Builder
    public Inquiry(String title, String content, User user) {
        this.title = title;
        this.content = content;
        this.user = user;
    }

    public void updateInquiry(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void answerInquiry(String answer) {
        this.answer = answer;
        this.answeredAt = LocalDateTime.now();
    }
}
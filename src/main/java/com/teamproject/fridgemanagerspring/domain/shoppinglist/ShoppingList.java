package com.teamproject.fridgemanagerspring.domain.shoppinglist;

import com.teamproject.fridgemanagerspring.domain.common.BaseTimeEntity;
import com.teamproject.fridgemanagerspring.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShoppingList extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String memo;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private Boolean isChecked = false; // @default(false)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE) // 데이터베이스 단의 Cascade 삭제 적용
    private User user;

    @Builder
    public ShoppingList(String memo, LocalDate date, User user) {
        this.memo = memo;
        this.date = date;
        this.user = user;
    }

    public void updateItem(String memo, LocalDate date) {
        this.memo = memo;
        this.date = date;
    }

    // 체크 상태 변경용 비즈니스 메서드
    public void toggleChecked() {
        this.isChecked = !this.isChecked;
    }
}

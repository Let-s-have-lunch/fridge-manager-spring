package com.teamproject.fridgemanagerspring.domain.user;

import com.teamproject.fridgemanagerspring.domain.category.Category;
import com.teamproject.fridgemanagerspring.domain.common.BaseTimeEntity;
import com.teamproject.fridgemanagerspring.domain.enums.RoleType;
import com.teamproject.fridgemanagerspring.domain.fridge.Fridge;
import com.teamproject.fridgemanagerspring.domain.shoppinglist.ShoppingList;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)  // JPA 기본 생성자 필수
public class User extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    private LocalDate birthdate;
    private Boolean isDeleted = false;
    private LocalDateTime deletedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoleType role = RoleType.USER;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Category> categories = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Fridge> fridges = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<ShoppingList> shoppingLists = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<com.example.fridgemanagerspring.domain.inquiry.Inquiry> inquiries = new ArrayList<>();

    @Builder
    private User(String nickname, String password, String email, LocalDate birthdate, RoleType role) {
        this.nickname = nickname;
        this.password = password;
        this.email = email;
        this.birthdate = birthdate;
        if (role != null) this.role = role;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updateBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void markAsDeleted() {
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();
        // 재가입 허용 및 개인정보 보호를 위한 익명화 처리
        this.email = "deleted_" + this.id + "@deleted.local";
        this.password = "";
    }
}
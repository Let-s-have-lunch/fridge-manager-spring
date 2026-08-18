package com.teamproject.fridgemanagerspring.domain.user;

import com.teamproject.fridgemanagerspring.domain.common.BaseTimeEntity;
import com.teamproject.fridgemanagerspring.domain.enums.RoleType;
import com.teamproject.fridgemanagerspring.domain.fridge.Fridge;
import com.teamproject.fridgemanagerspring.domain.inquriy.Inquiry;
import com.teamproject.fridgemanagerspring.domain.shoppinglist.ShoppingList;
import jakarta.persistence.*;
import jdk.jfr.Category;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_user_email", columnList = "email"),
        @Index(name = "idx_user_nickname", columnList = "nickname"),
        @Index(name = "idx_user_phone", columnList = "birthdate"),
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)  // JPA 기본 생성자 필수
@SQLDelete(sql = "UPDATE users SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
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

    private LocalDate birthdate; // DateTime? 매핑

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
    private List<Inquiry> inquiries = new ArrayList<>();

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
}
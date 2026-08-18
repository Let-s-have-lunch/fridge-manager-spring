package com.teamproject.fridgemanagerspring.dto.admin.response;

import com.teamproject.fridgemanagerspring.domain.enums.RoleType;
import com.teamproject.fridgemanagerspring.domain.user.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class AdminDashboardResponse {

    private List<RecentUser> recentUsers;

    @Getter
    @Builder
    public static class RecentUser {
        private Long id;
        private String nickname;
        private String email;
        private RoleType role;
        private LocalDateTime createdAt;

        public static RecentUser from(User user) {
            return RecentUser.builder()
                    .id(user.getId())
                    .nickname(user.getNickname())
                    .email(user.getEmail())
                    .role(user.getRole())
                    .createdAt(user.getCreatedAt())
                    .build();
        }
    }
}
package com.teamproject.fridgemanagerspring.repository;

import com.teamproject.fridgemanagerspring.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// 유저 담당 팀원분!!
// 이파일은 유저를 참조하는 파일이 너무 많아서 임시로 생성해둔 더미파일 입니다.
// 작업시작하면, 이 파일 내용 지우고 다시 작성하시면 됩니다.!

public interface UserRepository extends JpaRepository<User, Long> {

    // 1. Prisma: prisma.user.findUnique({ where: { email } })
    // JPA: 메서드 이름만으로 쿼리를 자동 생성합니다. (Query Method)
    Optional<User> findByEmail(String email);

    // 2. Prisma: nickname이 존재하는지 개수 카운트로 확인하던 로직
    // JPA: existsBy로 간단하게 존재 여부만 체크 (회원가입 시 중복 검사용)
    boolean existsByNickname(String nickname);

    // 내 ID를 제외하고 해당 닉네임이 존재하는지 검사 (닉네임 수정 시)
    boolean existsByNicknameAndIdNot(String nickname, Long id);
}
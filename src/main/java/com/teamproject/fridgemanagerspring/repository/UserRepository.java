package com.teamproject.fridgemanagerspring.repository;

import com.teamproject.fridgemanagerspring.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

// JpaRepository<엔티티 클래스, PK 데이터 타입>을 상속받습니다.
public interface UserRepository extends JpaRepository<User, Long> {

    // 1. Prisma: prisma.user.findUnique({ where: { email } })
    // JPA: 메서드 이름만으로 쿼리를 자동 생성합니다. (Query Method)
    Optional<User> findByEmail(String email);

    // 2. Prisma: nickname이 존재하는지 개수 카운트로 확인하던 로직
    // JPA: existsBy로 간단하게 존재 여부만 체크 (회원가입 시 중복 검사용)
    boolean existsByNickname(String nickname);

    // 내 ID를 제외하고 해당 닉네임이 존재하는지 검사 (닉네임 수정 시)
    boolean existsByNicknameAndIdNot(String nickname, Long id);

    List<User> findTop5ByOrderByCreatedAtDesc();

    Page<User> findAllByOrderByDesc(Pageable pageable);
}
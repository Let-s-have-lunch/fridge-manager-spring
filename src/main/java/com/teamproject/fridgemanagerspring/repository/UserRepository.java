package com.teamproject.fridgemanagerspring.repository;

import com.teamproject.fridgemanagerspring.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

// JpaRepository<엔티티 클래스, PK 데이터 타입>을 상속받습니다.
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByNickname(String nickname);

    boolean existsByNicknameAndIdNot(String nickname, Long id);

    // 최근 가입자 5명
    List<User> findTop5ByOrderByCreatedAtDesc();

    // 전체 사용자 최신 가입순 + 페이징
    Page<User> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
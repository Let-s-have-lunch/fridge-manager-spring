package com.teamproject.fridgemanagerspring.repository;

import com.teamproject.fridgemanagerspring.domain.inquriy.Inquiry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {
    // 1. 유저 ID로 문의 목록 조회 (페이지네이션 적용)
    Page<Inquiry> findByUserIdOrderByIdDesc(Long userId, Pageable pageable);

    // 2. 단건 조회 (User 정보 JOIN FETCH)
    @Query("SELECT i FROM Inquiry i JOIN FETCH i.user WHERE i.id = :id")
    Optional<Inquiry> findByIdWithUser(@Param("id") Long id);
}
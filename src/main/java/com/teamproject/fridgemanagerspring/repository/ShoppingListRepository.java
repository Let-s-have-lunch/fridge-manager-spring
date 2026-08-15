package com.teamproject.fridgemanagerspring.repository;

import com.teamproject.fridgemanagerspring.domain.shoppingList.ShoppingList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ShoppingListRepository extends JpaRepository<ShoppingList, Long> {

    // 💡 날짜 하루 조회 & 등록순(id ASC) 정렬
    List<ShoppingList> findByUserIdAndDateOrderByIdAsc(Long userId, LocalDate date);

    // 권한 체크를 포함한 단건 상세 조회
    Optional<ShoppingList> findByIdAndUserId(Long id, Long userId);
}

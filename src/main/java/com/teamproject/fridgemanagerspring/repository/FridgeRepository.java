package com.teamproject.fridgemanagerspring.repository;

import com.teamproject.fridgemanagerspring.domain.fridge.Fridge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FridgeRepository extends JpaRepository<Fridge, Long> {

    List<Fridge> findAllByUserIdAndDeletedAtIsNullOrderByIdAsc(Long userId);

    boolean existsByUserIdAndNameAndDeletedAtIsNull(Long userId, String name);

    Optional<Fridge> findByIdAndUserIdAndDeletedAtIsNull(Long id, Long userId);

    boolean existsByUserIdAndNameAndDeletedAtIsNullAndIdNot(Long userId, String name, Long id);
}
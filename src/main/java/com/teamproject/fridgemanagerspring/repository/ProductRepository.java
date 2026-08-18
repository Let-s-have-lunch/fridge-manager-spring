package com.teamproject.fridgemanagerspring.repository;

import com.teamproject.fridgemanagerspring.domain.enums.ProductStatus;
import com.teamproject.fridgemanagerspring.domain.product.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    // 1. 특정 냉장고의 상품 목록 조회 (Category 포함, STORED 상태만)
    @Query("SELECT p FROM Product p JOIN FETCH p.category " +
            "WHERE p.fridge.id = :fridgeId AND p.fridge.user.id = :userId AND p.status = :status " +
            "ORDER BY p.createdAt DESC")
    List<Product> findProductsByFridgeIdAndStatus(
            @Param("fridgeId") Long fridgeId,
            @Param("userId") Long userId,
            @Param("status") ProductStatus status
    );

    // 2. 단건 상세 조회 (내 냉장고의 제품이 맞는지 권한 검사 포함)
    @Query("SELECT p FROM Product p JOIN FETCH p.category " +
            "WHERE p.id = :productId AND p.fridge.user.id = :userId")
    Optional<Product> findByIdAndUserId(
            @Param("productId") Long productId,
            @Param("userId") Long userId
    );

    // [통계 1] 특정 기간 내 상태별 갯수 및 가격 합계 추출
    @Query("SELECT p.status, COUNT(p.id), SUM(p.price) FROM Product p " +
            "WHERE p.fridge.id IN :fridgeIds " +
            "AND p.updatedAt >= :startDate AND p.updatedAt < :endDate " +
            "GROUP BY p.status")
    List<Object[]> getStatusStatsByFridgeIds(
            @Param("fridgeIds") List<Long> fridgeIds,
            @Param("startDate") java.time.LocalDateTime startDate,
            @Param("endDate") java.time.LocalDateTime endDate
    );

    // [통계 2] 특정 기간(이번 달)에 소비된 제품 중 갯수 및 가격 합계 기준 TOP N
    @Query("SELECT p.name, COUNT(p.id), SUM(p.price) FROM Product p " +
            "WHERE p.fridge.id IN :fridgeIds " +
            "AND p.status = 'CONSUMED' " +
            "AND p.updatedAt >= :startDate AND p.updatedAt < :endDate " +
            "GROUP BY p.name " +
            "ORDER BY COUNT(p.id) DESC, SUM(p.price) DESC")
    List<Object[]> getTopConsumedProducts(
            @Param("fridgeIds") List<Long> fridgeIds,
            @Param("startDate") java.time.LocalDateTime startDate,
            @Param("endDate") java.time.LocalDateTime endDate,
            Pageable pageable
    );

    // [통계 3] 이번 달 소비된 제품의 카테고리별 소비 금액 합계
    @Query("SELECT p.category.id, SUM(p.price) FROM Product p " +
            "WHERE p.fridge.id IN :fridgeIds " +
            "AND p.status = 'CONSUMED' " +
            "AND p.price IS NOT NULL " +
            "AND p.updatedAt >= :startDate AND p.updatedAt < :endDate " +
            "GROUP BY p.category.id " +
            "ORDER BY SUM(p.price) DESC")
    List<Object[]> getCategoryStats(
            @Param("fridgeIds") List<Long> fridgeIds,
            @Param("startDate") java.time.LocalDateTime startDate,
            @Param("endDate") java.time.LocalDateTime endDate
    );

    // [통계 4] 특정 기간에 버려진 제품의 총 금액 합산
    @Query("SELECT SUM(p.price) FROM Product p " +
            "WHERE p.fridge.id IN :fridgeIds " +
            "AND p.status = 'DISCARDED' " +
            "AND p.updatedAt >= :startDate AND p.updatedAt < :endDate")
    Integer getDiscardedTotalPrice(
            @Param("fridgeIds") List<Long> fridgeIds,
            @Param("startDate") java.time.LocalDateTime startDate,
            @Param("endDate") java.time.LocalDateTime endDate
    );
}
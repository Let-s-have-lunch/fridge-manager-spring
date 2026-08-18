package com.teamproject.fridgemanagerspring.repository;

import com.teamproject.fridgemanagerspring.domain.enums.ProductStatus;
import com.teamproject.fridgemanagerspring.domain.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p FROM Product p JOIN FETCH p.category " +
            "WHERE p.fridge.id = :fridgeId AND p.fridge.user.id = :userId AND p.status = :status " +
            "ORDER BY p.createdAt DESC")
    List<Product> findProductsByFridgeIdAndStatus(
            @Param("fridgeId") Long fridgeId,
            @Param("userId") Long userId,
            @Param("status") ProductStatus status
    );

    @Query("SELECT p FROM Product p JOIN FETCH p.category " +
            "WHERE p.id = :productId AND p.fridge.user.id = :userId")
    Optional<Product> findByIdAndUserId(
            @Param("productId") Long productId,
            @Param("userId") Long userId
    );
}
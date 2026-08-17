package com.teamproject.fridgemanagerspring.repository;

import com.teamproject.fridgemanagerspring.domain.category.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("SELECT c FROM Category c " +
            "WHERE c.deletedAt IS NULL " +
            "AND (c.isDefault = true OR c.user.id = :userId) " +
            "ORDER BY c.id ASC")
    List<Category> findCategoriesForUser(@Param("userId") Long userId);

    @Query("SELECT COUNT(c) > 0 FROM Category c " +
            "WHERE c.name = :name " +
            "AND c.deletedAt IS NULL " +
            "AND (c.isDefault = true OR c.user.id = :userId)")
    boolean existsByNameForUser(@Param("name") String name, @Param("userId") Long userId);

    @Query("SELECT COUNT(c) > 0 FROM Category c " +
            "WHERE c.name = :name " +
            "AND c.id != :categoryId " +
            "AND c.deletedAt IS NULL " +
            "AND (c.isDefault = true OR c.user.id = :userId)")
    boolean existsByNameForUserExcludeId(
            @Param("name") String name,
            @Param("userId") Long userId,
            @Param("categoryId") Long categoryId);

    Optional<Category> findByIdAndDeletedAtIsNull(Long id);
}
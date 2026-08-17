package com.teamproject.fridgemanagerspring.service;

import com.teamproject.fridgemanagerspring.domain.category.Category;
import com.teamproject.fridgemanagerspring.domain.user.User;
import com.teamproject.fridgemanagerspring.repository.CategoryRepository;
import com.teamproject.fridgemanagerspring.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<Category> getCategoryList(Long userId) {
        return categoryRepository.findCategoriesForUser(userId);
    }

    @Transactional
    public Category createCategory(Long userId, String name) {
        if (categoryRepository.existsByNameForUser(name, userId)) {
            throw new RuntimeException("DUPLICATED_CATEGORY");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("USER_NOT_FOUND"));

        Category category = Category.builder()
                .name(name)
                .user(user)
                .isDefault(false)
                .build();

        return categoryRepository.save(category);
    }

    @Transactional
    public Category updateCategory(Long userId, Long categoryId, String name) {
        Category category = categoryRepository.findByIdAndDeletedAtIsNull(categoryId)
                .orElseThrow(() -> new RuntimeException("CATEGORY_NOT_FOUND"));

        if (category.getIsDefault()) {
            throw new RuntimeException("CANNOT_MODIFY_DEFAULT_CATEGORY");
        }

        // user가 null인 경우(공용 카테고리 등) 방어 처리 포함
        if (category.getUser() == null || !category.getUser().getId().equals(userId)) {
            throw new RuntimeException("UNAUTHORIZED_ACCESS");
        }

        if (categoryRepository.existsByNameForUserExcludeId(name, userId, categoryId)) {
            throw new RuntimeException("DUPLICATED_CATEGORY");
        }

        category.updateName(name); // Dirty Checking
        return category;
    }

    @Transactional
    public void deleteCategory(Long userId, Long categoryId) {
        Category category = categoryRepository.findByIdAndDeletedAtIsNull(categoryId)
                .orElseThrow(() -> new RuntimeException("CATEGORY_NOT_FOUND"));

        if (category.getIsDefault()) {
            throw new RuntimeException("CANNOT_DELETE_DEFAULT_CATEGORY");
        }

        if (category.getUser() == null || !category.getUser().getId().equals(userId)) {
            throw new RuntimeException("UNAUTHORIZED_ACCESS");
        }

        category.markAsDeleted(); // Dirty Checking
    }
}
package com.teamproject.fridgemanagerspring.service;

import com.teamproject.fridgemanagerspring.domain.fridge.Fridge;
import com.teamproject.fridgemanagerspring.domain.user.User;
import com.teamproject.fridgemanagerspring.repository.FridgeRepository;
import com.teamproject.fridgemanagerspring.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FridgeService {
    private final FridgeRepository fridgeRepository;
    private final UserRepository userRepository; // FK 관계인 User를 가져오기 위해 필요

    @Transactional(readOnly = true)
    public List<Fridge> getFridgeList(Long userId) {
        return fridgeRepository.findAllByUserIdAndDeletedAtIsNullOrderByIdAsc(userId);
    }

    @Transactional
    public Fridge createFridge(Long userId, String name) {
        if (fridgeRepository.existsByUserIdAndNameAndDeletedAtIsNull(userId, name)) {
            throw new RuntimeException("ALREADY_EXISTS_NAME");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("USER_NOT_FOUND"));

        Fridge fridge = Fridge.builder()
                .name(name)
                .user(user)
                .build();

        return fridgeRepository.save(fridge);
    }

    @Transactional
    public Fridge updateFridge(Long userId, Long fridgeId, String name) {
        Fridge fridge = fridgeRepository.findByIdAndUserIdAndDeletedAtIsNull(fridgeId, userId)
                .orElseThrow(() -> new RuntimeException("NOT_FOUND_FRIDGE"));

        if (fridgeRepository.existsByUserIdAndNameAndDeletedAtIsNullAndIdNot(userId, name, fridgeId)) {
            throw new RuntimeException("ALREADY_EXISTS_NAME");
        }

        fridge.updateName(name); // Dirty Checking으로 자동 UPDATE
        return fridge;
    }

    @Transactional
    public void deleteFridge(Long userId, Long fridgeId) {
        Fridge fridge = fridgeRepository.findByIdAndUserIdAndDeletedAtIsNull(fridgeId, userId)
                .orElseThrow(() -> new RuntimeException("NOT_FOUND_FRIDGE"));

        fridge.markAsDeleted(); // Dirty Checking으로 자동 UPDATE
    }
}

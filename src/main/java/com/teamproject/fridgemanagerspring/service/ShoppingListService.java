package com.teamproject.fridgemanagerspring.service;


import com.teamproject.fridgemanagerspring.domain.shoppinglist.ShoppingList;
import com.teamproject.fridgemanagerspring.domain.user.User;
import com.teamproject.fridgemanagerspring.dto.shoppinglist.request.ShoppingListRequest;
import com.teamproject.fridgemanagerspring.repository.ShoppingListRepository;
import com.teamproject.fridgemanagerspring.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShoppingListService {

    private final ShoppingListRepository shoppingListRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<ShoppingList> getItems(Long userId, LocalDate targetDate) {
        return shoppingListRepository.findByUserIdAndDateOrderByIdAsc(userId, targetDate);
    }

    @Transactional(readOnly = true)
    private ShoppingList getItemById(Long userId, Long itemId) {
        return shoppingListRepository.findByIdAndUserId(itemId, userId)
                .orElseThrow(() -> new RuntimeException("NOT_FOUND_ITEM"));
    }

    @Transactional
    public ShoppingList createItem(Long userId, ShoppingListRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("USER_NOT_FOUND"));

        ShoppingList item = ShoppingList.builder()
                .memo(request.getMemo())
                .date(request.getDate())
                .user(user)
                .build();

        return shoppingListRepository.save(item);
    }

    @Transactional
    public ShoppingList updateItem(Long userId, Long itemId, ShoppingListRequest request) {
        ShoppingList item = getItemById(userId, itemId);
        item.updateItem(request.getMemo(), request.getDate());
        return item; // Dirty Checking
    }

    @Transactional
    public void deleteItem(Long userId, Long itemId) {
        ShoppingList item = getItemById(userId, itemId);
        shoppingListRepository.delete(item); // 하드 삭제
    }

    @Transactional
    public ShoppingList toggleTodo(Long userId, Long itemId) {
        ShoppingList item = getItemById(userId, itemId);
        item.toggleChecked();
        return item; // Dirty Checking
    }
}

package com.teamproject.fridgemanagerspring.service;

import com.teamproject.fridgemanagerspring.domain.fridge.Fridge;
import com.teamproject.fridgemanagerspring.domain.user.User;
import com.teamproject.fridgemanagerspring.dto.admin.user.request.AdminUserUpdateRequest;
import com.teamproject.fridgemanagerspring.dto.user.request.*;
import com.teamproject.fridgemanagerspring.repository.FridgeRepository;
import com.teamproject.fridgemanagerspring.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final FridgeRepository fridgeRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("USER_NOT_FOUND"));

        if (user.getDeletedAt() != null) {
            throw new RuntimeException("USER_NOT_FOUND");
        }
        return user;
    }

    @Transactional
    public User createUser(CreateUserRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("ALREADY_EXISTS_EMAIL");
        }

        if (userRepository.existsByNickname(request.getNickname())) {
            throw new RuntimeException("ALREADY_EXISTS_NICKNAME");
        }

        LocalDate parsedBirthdate = null;
        if (request.getBirthdate() != null && !request.getBirthdate().isBlank()) {
            parsedBirthdate = LocalDate.parse(request.getBirthdate(), DateTimeFormatter.ISO_DATE);
        }

        User user = User.builder()
                .nickname(request.getNickname())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .birthdate(parsedBirthdate)
                .build();
        userRepository.save(user);

        Fridge defaultFridge = Fridge.builder()
                .name("내 냉장고")
                .user(user)
                .build();
        fridgeRepository.save(defaultFridge);

        return user;
    }

    @Transactional(readOnly = true)
    public User login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("INVALID_CREDENTIAL"));

        if (user.getDeletedAt() != null) {
            throw new RuntimeException("INVALID_CREDENTIAL");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("INVALID_CREDENTIAL");
        }

        return user;
    }

    @Transactional
    public User updateUser(Long userId, UpdateUserRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("USER_NOT_FOUND"));

        if (user.getDeletedAt() != null) {
            throw new RuntimeException("USER_NOT_FOUND");
        }

        if (request.getNickname() != null) {
            if (userRepository.existsByNicknameAndIdNot(request.getNickname(), userId)) {
                throw new RuntimeException("DUPLICATED_NICKNAME");
            }
            user.updateNickname(request.getNickname());
        }

        if (request.getBirthdate() != null && !request.getBirthdate().isBlank()) {
            LocalDate parsedBirthdate = LocalDate.parse(request.getBirthdate(), DateTimeFormatter.ISO_DATE);
            user.updateBirthdate(parsedBirthdate);
        }

        return user;
    }

    @Transactional
    public void updatePassword(Long userId, UpdatePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("USER_NOT_FOUND"));

        if (!passwordEncoder.matches(request.getPrevPassword(), user.getPassword())) {
            throw new RuntimeException("INVALID_PASSWORD");
        }

        user.updatePassword(passwordEncoder.encode(request.getPassword()));
    }

    @Transactional
    public void resetPassword(String email, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("USER_NOT_FOUND"));

        if (user.getDeletedAt() != null) {
            throw new RuntimeException("USER_NOT_FOUND");
        }

        user.updatePassword(passwordEncoder.encode(newPassword));
    }

    @Transactional
    public void withdrawUser(Long userId, WithdrawUserRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("USER_NOT_FOUND"));

        if (user.getDeletedAt() != null) throw new RuntimeException("USER_NOT_FOUND");

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("INVALID_CREDENTIAL");
        }

        user.markAsDeleted();
    }

    @Transactional(readOnly = true)
    public Page<User> getUserList(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        return userRepository.findAllByOrderByCreatedAtDesc(pageable);
    }

    @Transactional
    public User adminUpdateUser(Long targetUserId, AdminUserUpdateRequest request) {
        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new RuntimeException("USER_NOT_FOUND"));

        if (request.getNickname() != null && !request.getNickname().equals(targetUser.getNickname())) {
            if (userRepository.existsByNicknameAndIdNot(request.getNickname(), targetUserId)) {
                throw new RuntimeException("ALREADY_EXISTS_NICKNAME");
            }
            targetUser.updateNickname(request.getNickname());
        }

        if (request.getEmail() != null && !request.getEmail().equals(targetUser.getEmail())) {
            if (userRepository.findByEmail(request.getEmail()).isPresent()) {
                throw new RuntimeException("ALREADY_EXISTS_EMAIL");
            }
            targetUser.updateEmail(request.getEmail());
        }

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            targetUser.updatePassword(passwordEncoder.encode(request.getPassword()));
        }

        if (request.getBirthdate() != null) {
            targetUser.updateBirthdate(request.getBirthdate());
        }

        if (request.getRole() != null) {
            targetUser.updateRole(request.getRole());
        }

        return targetUser;
    }

    @Transactional
    public User adminDeleteUser(Long targetUserId) {
        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new RuntimeException("USER_NOT_FOUND"));

        if (targetUser.getDeletedAt() != null) {
            throw new RuntimeException("USER_ALREADY_DELETED");
        }

        targetUser.markAsDeleted();
        return targetUser;
    }
}
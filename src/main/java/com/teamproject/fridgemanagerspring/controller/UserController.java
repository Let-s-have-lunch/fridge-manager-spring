package com.teamproject.fridgemanagerspring.controller;

import com.teamproject.fridgemanagerspring.domain.user.User;
import com.teamproject.fridgemanagerspring.dto.user.request.*;
import com.teamproject.fridgemanagerspring.service.UserService;
import com.teamproject.fridgemanagerspring.utils.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/users") // userRouter.ts의 역할
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final JwtUtil jwtUtil;

    @PostMapping("/create")
    // @Valid가 CreateUserRequest 내부의 조건들을 검사하고, 실패 시 GlobalExceptionHandler로 던집니다.
    public ResponseEntity createUser(@Valid @RequestBody CreateUserRequest request) {
        try {
            User newUser = userService.createUser(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "성공적으로 회원가입 되었습니다.", "data", newUser));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("ALREADY_EXISTS_EMAIL"))
                return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "이미 가입된 이메일입니다."));
            if (e.getMessage().equals("ALREADY_EXISTS_NICKNAME"))
                return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "이미 사용 중인 닉네임입니다."));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "서버 에러"));
        }
    }

    @PostMapping("/login")
    public ResponseEntity login(@Valid @RequestBody LoginRequest request) {
        try {
            User user = userService.login(request);
            String token = jwtUtil.generateToken(user.getId());

            return ResponseEntity.ok(
                    Map.of(
                            "message", "로그인에 성공했습니다.",
                            "data", Map.of("user", user, "token", token)
                    )
            );
        } catch (RuntimeException e) {
            if (e.getMessage().equals("INVALID_CREDENTIALS"))
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "아이디 또는 비밀번호가 일치하지 않습니다."));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "서버 에러"));
        }
    }

    @GetMapping("/me")
    public ResponseEntity getMe() {
        // TODO: Spring Security 설정 후 SecurityContext에서 가져올 예정
        Long currentUserId = 1L;
        try {
            User user = userService.getUserById(currentUserId);
            return ResponseEntity.ok(Map.of("message", "사용자 정보 확인이 완료되었습니다.", "data", user));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "유효하지 않은 사용자이거나 탈퇴한 계정입니다."));
        }
    }

    @PatchMapping("/update")
    public ResponseEntity updateUser(@Valid @RequestBody UpdateUserRequest request) {
        Long currentUserId = 1L;
        try {
            User updatedUser = userService.updateUser(currentUserId, request);
            return ResponseEntity.ok(Map.of("message", "회원 정보가 성공적으로 수정되었습니다.", "data", updatedUser));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("USER_NOT_FOUND"))
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "해당 사용자를 찾을 수 없습니다."));
            if (e.getMessage().equals("DUPLICATED_NICKNAME"))
                return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "이미 존재하는 닉네임입니다."));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "서버 에러"));
        }
    }

    @PatchMapping("/password")
    public ResponseEntity updatePassword(@Valid @RequestBody UpdatePasswordRequest request) {
        Long currentUserId = 1L;
        try {
            userService.updatePassword(currentUserId, request);
            return ResponseEntity.ok(Map.of("message", "비밀번호가 성공적으로 변경되었습니다."));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("USER_NOT_FOUND"))
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "해당 사용자를 찾을 수 없습니다."));
            if (e.getMessage().equals("INVALID_PASSWORD"))
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "현재 비밀번호가 일치하지 않습니다."));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "서버 에러"));
        }
    }

    @PostMapping("/password-reset")
    public ResponseEntity resetPassword(@RequestBody ResetPasswordRequest request) {
        try {
            userService.resetPassword(request.getEmail(), request.getNewPassword());
            return ResponseEntity.ok(Map.of("success", true, "message", "비밀번호가 성공적으로 변경되었습니다."));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "서버 에러"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity logout() {
        return ResponseEntity.ok(Map.of("message", "성공적으로 로그아웃되었습니다."));
    }

    @PatchMapping("/withdraw")
    public ResponseEntity withdrawUser(@Valid @RequestBody WithdrawUserRequest request) {
        Long currentUserId = 1L;
        try {
            userService.withdrawUser(currentUserId, request);
            return ResponseEntity.ok(Map.of("message", "회원 탈퇴가 성공적으로 처리되었습니다."));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("USER_NOT_FOUND"))
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "해당 사용자를 찾을 수 없습니다."));
            if (e.getMessage().equals("INVALID_PASSWORD"))
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "현재 비밀번호가 일치하지 않습니다."));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "서버 에러"));
        }
    }
}
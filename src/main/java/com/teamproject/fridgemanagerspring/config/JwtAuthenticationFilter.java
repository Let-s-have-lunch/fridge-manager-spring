package com.teamproject.fridgemanagerspring.config;

import com.teamproject.fridgemanagerspring.domain.user.User;
import com.teamproject.fridgemanagerspring.repository.UserRepository;
import com.teamproject.fridgemanagerspring.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;   // 데이터 조회를 위한 UserRepository 추가

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.substring(7);

            if (jwtUtil.validateToken(token)) {
                Long userId = jwtUtil.getUserIdFromToken(token);

                User user = userRepository.findById(userId).orElse(null);

                if (user != null && user.getDeletedAt() == null) {
                    SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.getRole().name());

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userId, null, Collections.singletonList(authority));

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}
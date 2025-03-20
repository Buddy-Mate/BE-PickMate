package com.Buddymate.pickMate.controller;

import com.Buddymate.pickMate.config.JwtTokenProvider;
import com.Buddymate.pickMate.dto.UserResponseDto;
import com.Buddymate.pickMate.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    @GetMapping("/auth/my")
    public ResponseEntity<?> getUserInfo(HttpServletRequest request) {
        // 요청 헤더에서 Authorization 가져오기
        String token = extractTokenFromRequest(request);

        if (token == null) {
            log.warn("Authorization 헤더가 없거나 형식이 올바르지 않음");
            return ResponseEntity.status(401).body("토큰이 필요합니다.");
        }

        // 토큰 검증
        if (!jwtTokenProvider.validateToken(token)) {
            log.warn("JWT 검증 실패: {}", token);
            return ResponseEntity.status(401).body("유효하지 않은 토큰입니다.");
        }

        // 토큰에서 이메일 추출
        String email = jwtTokenProvider.getEmailFromToken(token);

        UserResponseDto userInfo = userService.getUserByEmail(email);

        return ResponseEntity.ok(userInfo);
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }
}

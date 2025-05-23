package com.Buddymate.pickMate.controller;

import com.Buddymate.pickMate.config.JwtTokenProvider;
import com.Buddymate.pickMate.dto.AuthResponseDto;
import com.Buddymate.pickMate.dto.LoginRequestDto;
import com.Buddymate.pickMate.dto.SignupRequestDto;
import com.Buddymate.pickMate.entity.User;
import com.Buddymate.pickMate.exception.BusinessException;
import com.Buddymate.pickMate.exception.CustomException;
import com.Buddymate.pickMate.exception.ErrorCode;
import com.Buddymate.pickMate.repository.UserRepository;
import com.Buddymate.pickMate.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;


    // 회원가입 API
    @PostMapping("/signup")
    public ResponseEntity<?> register(@RequestBody SignupRequestDto request) {
        userService.registerUser(request.getEmail(), request.getNickname(), request.getPassword());
        return ResponseEntity.status(HttpStatus.CREATED).body("회원가입이 성공적으로 완료되었습니다!");
    }

    // 로그인 API
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException(ErrorCode.LOGIN_INVALID_EMAIL));

        // 비밀번호 검증
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.LOGIN_INVALID_PASSWORD);
        }

        // 이메일, 비밀번호 인증 시 JWT 토큰 생성
        String token = jwtTokenProvider.generateToken(user.getEmail());

        return ResponseEntity.ok(new AuthResponseDto(token, user.getNickname()));
    }
}

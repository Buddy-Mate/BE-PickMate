package com.Buddymate.pickMate.service;

import com.Buddymate.pickMate.dto.UserResponseDto;
import com.Buddymate.pickMate.dto.UserUpdateRequestDto;
import com.Buddymate.pickMate.entity.User;
import com.Buddymate.pickMate.exception.CustomException;
import com.Buddymate.pickMate.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User registerUser(String email, String nickname , String password) {

        // 이메일 중복 체크
        if (userRepository.findByEmail(email).isPresent()) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "이미 존재하는 이메일입니다.");

        }

        // 닉네임 중복 체크
        if (userRepository.findByNickname(nickname).isPresent()) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "이미 존재하는 닉네임입니다.");
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(password);

        User user = User.builder()
                .email(email)
                .nickname(nickname)
                .password(encodedPassword)
                .createdAt(new Date())
                .build();

        return userRepository.save(user);
    }

    // 사용자 정보 조회 메소드
    public UserResponseDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없음"));

        return new UserResponseDto(user);
    }

    // User 정보 수정
    @Transactional
    public void updateUser(String email, UserUpdateRequestDto dto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (dto.getNickname() != null && !user.getNickname().equals(dto.getNickname())) {
            if (userRepository.existsByNickname(dto.getNickname())) {
                throw new IllegalArgumentException("이미 사용중인 닉네임입니다.");
            }

            user.setNickname(dto.getNickname());
        }

        user.setIntroduction(dto.getIntroduction());
    }
}

package com.Buddymate.pickMate.service;

import com.Buddymate.pickMate.entity.User;
import com.Buddymate.pickMate.exception.CustomException;
import com.Buddymate.pickMate.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
                .build();

        return userRepository.save(user);
    }
}

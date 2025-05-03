package com.Buddymate.pickMate.service;

import com.Buddymate.pickMate.dto.UserResponseDto;
import com.Buddymate.pickMate.dto.UserUpdateRequestDto;
import com.Buddymate.pickMate.entity.User;
import com.Buddymate.pickMate.exception.BusinessException;
import com.Buddymate.pickMate.exception.CustomException;
import com.Buddymate.pickMate.exception.ErrorCode;
import com.Buddymate.pickMate.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final S3Service s3Service;

    public User registerUser(String email, String nickname , String password) {

        // 이메일 중복 체크
        if (userRepository.findByEmail(email).isPresent()) {
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
        }

        // 닉네임 중복 체크
        if (userRepository.findByNickname(nickname).isPresent()) {
            throw new BusinessException(ErrorCode.DUPLICATE_NICKNAME);
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
        User user = userRepository.findByEmail(email).
                orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        return new UserResponseDto(user);
    }

    // User 정보 수정
    @Transactional
    public void updateUser(String email, UserUpdateRequestDto dto, MultipartFile imageFile) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 닉네임 중복 확인
        if (dto.getNickname() != null && !user.getNickname().equals(dto.getNickname())) {
            if (userRepository.existsByNickname(dto.getNickname())) {
                throw new BusinessException(ErrorCode.DUPLICATE_NICKNAME);
            }

            user.setNickname(dto.getNickname());
        }

        // 한 줄 소개 업데이트
        if (dto.getIntroduction() != null && !dto.getIntroduction().equals(user.getIntroduction())) {
            user.setIntroduction(dto.getIntroduction());
        }

        // 이미지가 들어온 경우
        if (imageFile != null && !imageFile.isEmpty()) {
            try {

                // 기존 이미지 URL과 다를 경우만 업로드 수행
                String uploadedUrl = s3Service.upload(imageFile, "profile-images");

                // 업로드된 이미지가 기존 이미지와 다르면 갱신
                if (!uploadedUrl.equals(user.getProfileImageUrl())) {
                    user.setProfileImageUrl(uploadedUrl);
                }
            } catch (IOException e) {
                throw new BusinessException(ErrorCode.FILE_UPLOAD_FAIL);
            }
        }
    }
}

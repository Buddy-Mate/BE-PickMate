package com.Buddymate.pickMate.dto;

import com.Buddymate.pickMate.entity.User;
import lombok.Getter;

@Getter
public class UserResponseDto {
    private String email;
    private String nickname;
    private String createdAt;

    public UserResponseDto(User user) {
        this.email = user.getEmail();
        this.nickname = user.getNickname();
        this.createdAt = user.getCreatedAt().toString();
    }
}

package com.Buddymate.pickMate.repository;


import com.Buddymate.pickMate.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email); // 이메일로 유저 찾기

    Optional<User> findByNickname(String nickname); // 닉네임 중복 확인
}

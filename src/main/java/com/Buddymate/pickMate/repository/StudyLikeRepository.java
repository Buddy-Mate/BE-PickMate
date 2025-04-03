package com.Buddymate.pickMate.repository;

import com.Buddymate.pickMate.entity.Study;
import com.Buddymate.pickMate.entity.StudyLike;
import com.Buddymate.pickMate.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudyLikeRepository extends JpaRepository<StudyLike, Long> {
    // 스터디 좋아요 여부 조회
    Optional<StudyLike> findByUserAndStudy(User user, Study study);

}

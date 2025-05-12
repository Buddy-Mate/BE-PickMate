package com.Buddymate.pickMate.repository;

import com.Buddymate.pickMate.entity.Project;
import com.Buddymate.pickMate.entity.Study;
import com.Buddymate.pickMate.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface StudyRepository extends JpaRepository<Study, Long> {
    List<Study> findByAuthor(User author);

    List<Study> findByTitleContainingIgnoreCase(String keyword);

    List<Study> findByDeadlineBeforeAndExpiredStatusFalse(LocalDateTime deadline);
}

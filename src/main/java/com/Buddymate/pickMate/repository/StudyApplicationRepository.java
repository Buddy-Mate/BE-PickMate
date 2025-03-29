package com.Buddymate.pickMate.repository;

import com.Buddymate.pickMate.entity.Study;
import com.Buddymate.pickMate.entity.StudyApplication;
import com.Buddymate.pickMate.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudyApplicationRepository extends JpaRepository<StudyApplication, Long> {

    List<StudyApplication> findByApplicant (User user);

    List<StudyApplication> findByStudy_Author(User user);

    List<StudyApplication> findByStudy(Study study);

    Optional<StudyApplication> findByIdAndStudy_Author(Long id, User author);

    Optional<StudyApplication> findByStudyAndApplicant(Study study, User user);
}

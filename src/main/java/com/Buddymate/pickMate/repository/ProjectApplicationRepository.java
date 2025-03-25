package com.Buddymate.pickMate.repository;

import com.Buddymate.pickMate.entity.Project;
import com.Buddymate.pickMate.entity.ProjectApplication;
import com.Buddymate.pickMate.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectApplicationRepository extends JpaRepository<ProjectApplication, Long> {

    List<ProjectApplication> findByApplicant(User user);

    List<ProjectApplication> findByProject_Author(User user);

    List<ProjectApplication> findByProject(Project project);

    Optional<ProjectApplication> findByIdAndProject_Author(Long id, User author);

    Optional<ProjectApplication> findByProjectAndApplicant(Project project, User user);
}

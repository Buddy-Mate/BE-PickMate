package com.Buddymate.pickMate.repository;


import com.Buddymate.pickMate.entity.Project;
import com.Buddymate.pickMate.entity.ProjectLike;
import com.Buddymate.pickMate.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProjectLikeRepository extends JpaRepository<ProjectLike, Long> {
    // 프로젝트 좋아요 여부 조회
    Optional<ProjectLike> findByUserAndProject(User user, Project project);

}

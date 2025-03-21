package com.Buddymate.pickMate.repository;

import com.Buddymate.pickMate.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByTitleContaining(String keyword);

    // 조회수 증가 쿼리
    @Modifying
    @Query("UPDATE Project p SET p.views = p.views + 1 WHERE p.id = :id")
    void increaseViewCount(@Param("id") Long id);
}

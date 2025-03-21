package com.Buddymate.pickMate.service;

import com.Buddymate.pickMate.dto.ProjectDto;
import com.Buddymate.pickMate.entity.Project;
import com.Buddymate.pickMate.entity.User;
import com.Buddymate.pickMate.repository.ProjectRepository;
import com.Buddymate.pickMate.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    // 프로젝트 생성 (트랜잭션 처리)
    @Transactional
    public ProjectDto.Response createProject(String email, ProjectDto.CreateRequest request) {
        User author = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Project project = Project.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .author(author)
                .techStack(request.getTechStack())
                .deadline(request.getDeadline())
                .build();

        Project savedProject = projectRepository.save(project);
        return new ProjectDto.Response(savedProject);
    }

    // 프로젝트 전체 조회
    @Transactional(readOnly = true)
    public List<ProjectDto.Response> getAllProjects() {
        return projectRepository.findAll().stream()
                .map(ProjectDto.Response::new)
                .collect(Collectors.toList());
    }

    // 프로젝트 단일 조회
    @Transactional
    public ProjectDto.Response getProjectById(Long id) {

        // 조회수 증가
        projectRepository.increaseViewCount(id);

        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("프로젝트를 찾을 수 없습니다."));

        return new ProjectDto.Response(project);
    }

    // 프로젝트 수정
    @Transactional
    public ProjectDto.Response updateProject(String email, Long id, ProjectDto.CreateRequest request) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("프로젝트를 찾을 수 없습니다."));

        if (!project.getAuthor().getEmail().equals(email)) {
            throw new IllegalArgumentException("수정 권한이 없습니다.");
        }

        project.setTitle(request.getTitle());
        project.setDescription(request.getDescription());
        project.setTechStack(request.getTechStack());
        project.setDeadline(request.getDeadline());

        return new ProjectDto.Response(project);
    }

    // 프로젝트 삭제
    @Transactional
    public void deleteProject(String email, Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("프로젝트를 찾을 수 없습니다."));

        if (!project.getAuthor().getEmail().equals(email)) {
            throw new IllegalArgumentException("삭제 권한이 없습니다.");
        }

        projectRepository.delete(project);
    }
}

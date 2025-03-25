package com.Buddymate.pickMate.service;

import com.Buddymate.pickMate.dto.ProjectDto;
import com.Buddymate.pickMate.entity.Project;
import com.Buddymate.pickMate.entity.ProjectApplication;
import com.Buddymate.pickMate.entity.User;
import com.Buddymate.pickMate.enums.ApplicationStatus;
import com.Buddymate.pickMate.repository.ProjectApplicationRepository;
import com.Buddymate.pickMate.repository.ProjectRepository;
import com.Buddymate.pickMate.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectApplicationRepository projectApplicationRepository;

    // 프로젝트 생성 (트랜잭션 처리)
    @Transactional
    public ProjectDto.Response createProject(String email, ProjectDto.CreateRequest request) {
        User author = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Project project = Project.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .author(author)
                .techStack(ProjectDto.convertListToString(request.getTechStack()))
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
    public ProjectDto.Response getProjectById(Long id, String email) {

        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("프로젝트를 찾을 수 없습니다."));

        project.setViews(project.getViews() + 1);

        ApplicationStatus applicationStatus = ApplicationStatus.NOT_APPLIED;

        if (email != null && !email.isBlank()) {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

            Optional<ProjectApplication> applicationOpt =
                    projectApplicationRepository.findByProjectAndApplicant(project, user);

            applicationStatus = applicationOpt.map(ProjectApplication::getStatus)
                    .orElse(ApplicationStatus.NOT_APPLIED);
        }

        ProjectDto.Response response = new ProjectDto.Response(project);
        response.setApplicationStatus(applicationStatus);

        return response;
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
        project.setTechStack(ProjectDto.convertListToString(request.getTechStack()));
        project.setDeadline(request.getDeadline());
        project.setUpdatedAt(LocalDateTime.now());

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

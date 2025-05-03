package com.Buddymate.pickMate.service;

import com.Buddymate.pickMate.dto.ProjectDto;
import com.Buddymate.pickMate.entity.Project;
import com.Buddymate.pickMate.entity.ProjectApplication;
import com.Buddymate.pickMate.entity.ProjectLike;
import com.Buddymate.pickMate.entity.User;
import com.Buddymate.pickMate.enums.ApplicationStatus;
import com.Buddymate.pickMate.exception.BusinessException;
import com.Buddymate.pickMate.exception.ErrorCode;
import com.Buddymate.pickMate.repository.ProjectApplicationRepository;
import com.Buddymate.pickMate.repository.ProjectLikeRepository;
import com.Buddymate.pickMate.repository.ProjectRepository;
import com.Buddymate.pickMate.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectApplicationRepository projectApplicationRepository;
    private final ProjectLikeRepository projectLikeRepository;

    // 프로젝트 생성 (트랜잭션 처리)
    @Transactional
    public ProjectDto.Response createProject(String email, ProjectDto.CreateRequest request) {
        User author = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

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
                .collect(toList());
    }

    // 프로젝트 단일 조회
    @Transactional
    public ProjectDto.Response getProjectById(Long id, String email) {

        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROJECT_NOT_FOUND));

        project.setViews(project.getViews() + 1);

        ApplicationStatus applicationStatus = ApplicationStatus.NOT_APPLIED;

        if (email != null && !email.isBlank()) {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

            Optional<ProjectApplication> applicationOpt =
                    projectApplicationRepository.findByProjectAndApplicant(project, user);

            applicationStatus = applicationOpt.map(ProjectApplication::getStatus)
                    .orElse(ApplicationStatus.NOT_APPLIED);
        }

        ProjectDto.Response response = new ProjectDto.Response(project);
        response.setApplicationStatus(applicationStatus);

        return response;
    }

    // 내가 등록한 프로젝트 조회
    @Transactional(readOnly = true)
    public List<ProjectDto.Response> getProjectsByAuthor(String email) {
        User author = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        return projectRepository.findByAuthor(author).stream()
                .map(ProjectDto.Response::new)
                .collect(toList());
    }

    // 프로젝트 수정
    @Transactional
    public ProjectDto.Response updateProject(String email, Long id, ProjectDto.CreateRequest request) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROJECT_NOT_FOUND));

        if (!project.getAuthor().getEmail().equals(email)) {
            throw new BusinessException(ErrorCode.PROJECT_INVALID_UPDATE);
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
                .orElseThrow(() -> new BusinessException(ErrorCode.PROJECT_NOT_FOUND));

        if (!project.getAuthor().getEmail().equals(email)) {
            throw new BusinessException(ErrorCode.PROJECT_INVALID_DELETE);
        }

        projectRepository.delete(project);
    }

    // 프로젝트 좋아요
    @Transactional
    public void likeProject(String email, Long projectId) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROJECT_NOT_FOUND));

        if (projectLikeRepository.findByUserAndProject(user, project).isPresent()) {
            throw new BusinessException(ErrorCode.PROJECT_LIKE_DUPLICATE);
        }

        projectLikeRepository.save(ProjectLike.builder()
                .user(user)
                .project(project)
                .build());

        project.setLikes(project.getLikes() + 1);

    }

    // 프로젝트 좋아요 취소
    @Transactional
    public void unlikeProject(String email, Long projectId) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROJECT_NOT_FOUND));

        ProjectLike like = projectLikeRepository.findByUserAndProject(user, project)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROJECT_LIKE_NOT_FOUND));

        projectLikeRepository.delete(like);

        project.setLikes(project.getLikes() - 1);
    }

    // 프로젝트 검색
    @Transactional(readOnly = true)
    public List<ProjectDto.Response> searchProjectsByKeyword(String keyword) {

        List<Project> projectList = projectRepository.findByTitleContainingIgnoreCase(keyword);

        return projectList.stream()
                .map(ProjectDto.Response::new)
                .collect(toList());
    }
}

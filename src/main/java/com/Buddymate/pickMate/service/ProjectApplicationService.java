package com.Buddymate.pickMate.service;

import com.Buddymate.pickMate.dto.ProjectApplicationDto;
import com.Buddymate.pickMate.entity.Project;
import com.Buddymate.pickMate.entity.ProjectApplication;
import com.Buddymate.pickMate.entity.User;
import com.Buddymate.pickMate.enums.ApplicationStatus;
import com.Buddymate.pickMate.repository.ProjectApplicationRepository;
import com.Buddymate.pickMate.repository.ProjectRepository;
import com.Buddymate.pickMate.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectApplicationService {

    private final ProjectRepository projectRepository;
    private final ProjectApplicationRepository projectApplicationRepository;
    private final UserRepository userRepository;


    // 프로젝트 신청
    public ProjectApplicationDto.Response apply(String email, Long projectId, ProjectApplicationDto.CreateRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("프로젝트를 찾을 수 없습니다."));

        if (project.getAuthor().getUserId().equals(user.getUserId())) {
            throw new IllegalStateException("자신의 프로젝트에는 신청할 수 없습니다.");
        }

        ProjectApplication application = ProjectApplication.builder()
                .applicant(user)
                .project(project)
                .message(request.getMessage())
                .build();

        ProjectApplication savedApplication = projectApplicationRepository.save(application);

        return toDto(savedApplication);
    }

    // 내 프로젝트에 신청한 지원자
    @Transactional(readOnly = true)
    public List<ProjectApplicationDto.Response> getApplicationsByApplicant(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));

        return projectApplicationRepository.findByApplicant(user).stream()
                .map(this::toDto)
                .collect(toList());
    }

    @Transactional(readOnly = true)
    public List<ProjectApplicationDto.Response> getApplicationsByAuthor(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));

        return projectApplicationRepository.findByProject_Author(user).stream()
                .map(this::toDto)
                .collect(toList());
    }

    @Transactional(readOnly = true)
    public List<ProjectApplicationDto.Response> getApplicationsByProjectIdAndAuthor(String email, Long projectId) {
        User author = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("작성자 없음"));
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("프로젝트 없음"));

        if (!project.getAuthor().getUserId().equals(author.getUserId())) {
            throw new IllegalArgumentException("신청자 목록을 조회할 권한이 없습니다.");
        }

        return projectApplicationRepository.findByProject(project).stream()
                .map(this::toDto)
                .collect(toList());
    }

    @Transactional
    public void acceptApplication(String email,Long applicationId, String openLink) {
        User author = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("작성자 정보 없음"));

        ProjectApplication app = projectApplicationRepository.findByIdAndProject_Author(applicationId, author)
                .orElseThrow(() -> new EntityNotFoundException("신청 정보 없음"));

        app.setStatus(ApplicationStatus.ACCEPTED);
        app.setOpenLink(openLink);
    }

    @Transactional
    public void rejectApplication(String email, Long applicationId) {
        User author = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("작성자 정보 없음"));

        ProjectApplication app = projectApplicationRepository.findByIdAndProject_Author(applicationId, author)
                .orElseThrow(() -> new EntityNotFoundException("신청 정보 없음"));

        app.setStatus(ApplicationStatus.REJECTED);
    }

    @Transactional
    public void cancelApplication(Long applicationId, String eamil) {
        User user = userRepository.findByEmail(eamil)
                .orElseThrow(() -> new IllegalArgumentException("유저 정보 없음"));

        ProjectApplication projectApplication = projectApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("신청 정보 없음"));


        // 본인 신청인지 확인
        if (!projectApplication.getApplicant().getUserId().equals(user.getUserId())) {
            throw new IllegalArgumentException("본인 신청만 취소할 수 있습니다.");
        }

        projectApplicationRepository.delete(projectApplication);
    }

    private ProjectApplicationDto.Response toDto(ProjectApplication app) {
        return ProjectApplicationDto.Response.builder()
                .applicationId(app.getId())
                .projectId(app.getProject().getId())
                .projectTitle(app.getProject().getTitle())
                .applicantNickname(app.getApplicant().getNickname())
                .message(app.getMessage())
                .status(app.getStatus())
                .openLink(app.getOpenLink())
                .createdAt(app.getCreatedAt())
                .build();
    }
}

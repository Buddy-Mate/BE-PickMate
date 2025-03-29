package com.Buddymate.pickMate.service;

import com.Buddymate.pickMate.dto.StudyDto;
import com.Buddymate.pickMate.entity.Study;
import com.Buddymate.pickMate.entity.StudyApplication;
import com.Buddymate.pickMate.entity.User;
import com.Buddymate.pickMate.enums.ApplicationStatus;
import com.Buddymate.pickMate.repository.StudyApplicationRepository;
import com.Buddymate.pickMate.repository.StudyRepository;
import com.Buddymate.pickMate.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class StudyService {

    private final StudyRepository studyRepository;
    private final UserRepository userRepository;
    private final StudyApplicationRepository studyApplicationRepository;

    // 스터디 게시글 생성
    @Transactional
    public StudyDto.Response createStudy(String email, StudyDto.CreateRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("사용자 정보 없음"));

        Study study = Study.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .deadline(request.getDeadline())
                .author(user)
                .build();

        Study savedStudy = studyRepository.save(study);

        return new StudyDto.Response(savedStudy);
    }

    // 스터디 게시글 전체 조회
    @Transactional(readOnly = true)
    public List<StudyDto.Response> getAllStudies() {
        return studyRepository.findAll().stream()
                .map(StudyDto.Response:: new)
                .toList();
    }

    // 스터디 게시글 단일 조회
    @Transactional
    public StudyDto.Response getStudyById(Long id, String email) {
        Study study = studyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("스터디를 찾을 수 없습니다."));

        study.setViews(study.getViews() + 1); //조회수 1 증가

        ApplicationStatus applicationStatus = ApplicationStatus.NOT_APPLIED;

        if (email != null && !email.isBlank()) {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

            Optional<StudyApplication> applicationOpt =
                    studyApplicationRepository.findByStudyAndApplicant(study, user);

            applicationStatus = applicationOpt.map(StudyApplication::getStatus)
                    .orElse(ApplicationStatus.NOT_APPLIED);
        }

        StudyDto.Response response = new StudyDto.Response(study);
        response.setApplicationStatus(applicationStatus);

        return response;
    }

    // 내 스터디 게시글 조회
    @Transactional(readOnly = true)
    public List<StudyDto.Response> getStudiesByAuthor(String email) {
        User author = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("유저 정보 없음"));

        return studyRepository.findByAuthor(author).stream()
                .map(StudyDto.Response::new)
                .collect(toList());

    }

    // 스터디 게시글 수정
    @Transactional
    public StudyDto.Response updateStudy(String email, Long id, StudyDto.CreateRequest request) {
        Study study = studyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("스터디를 찾을 수 없습니다."));

        if (!study.getAuthor().getEmail().equals(email)) {
            throw new SecurityException("수정 권한이 없습니다.");
        }

        study.setTitle(request.getTitle());
        study.setDescription(request.getDescription());
        study.setDeadline(request.getDeadline());
        study.setUpdatedAt(LocalDateTime.now());

        return new StudyDto.Response(study);
    }

    // 게시글 삭제
    @Transactional
    public void deleteStudy(String email, Long id) {
        Study study = studyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("스터디를 찾을 수 없습니다."));

        if (!study.getAuthor().getEmail().equals(email)) {
            throw new SecurityException("삭제 권한이 없습니다.");
        }

        studyRepository.delete(study);
    }
}

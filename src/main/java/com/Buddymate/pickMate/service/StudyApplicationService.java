package com.Buddymate.pickMate.service;

import com.Buddymate.pickMate.dto.StudyApplicationDto;
import com.Buddymate.pickMate.entity.Study;
import com.Buddymate.pickMate.entity.User;
import com.Buddymate.pickMate.entity.StudyApplication;
import com.Buddymate.pickMate.enums.ApplicationStatus;
import com.Buddymate.pickMate.exception.BusinessException;
import com.Buddymate.pickMate.exception.ErrorCode;
import com.Buddymate.pickMate.repository.StudyRepository;
import com.Buddymate.pickMate.repository.UserRepository;
import com.Buddymate.pickMate.repository.StudyApplicationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Transactional
public class StudyApplicationService {

    private final StudyApplicationRepository studyApplicationRepository;
    private final StudyRepository studyRepository;
    private final UserRepository userRepository;

    // 스터디 신청
    public StudyApplicationDto.Response apply(String email, Long studyId, StudyApplicationDto.CreateRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STUDY_NOT_FOUND));

        if (study.getAuthor().getUserId().equals(user.getUserId())) {
            throw new BusinessException(ErrorCode.STUDY_SELF_APPLICATION_NOT_ALLOWED);
        }

        StudyApplication application = StudyApplication.builder()
                .applicant(user)
                .study(study)
                .message(request.getMessage())
                .build();

        StudyApplication savedApplication = studyApplicationRepository.save(application);

        return toDto(savedApplication);
    }

    // 내 스터디에 신청한 지원자
    @Transactional(readOnly = true)
    public List<StudyApplicationDto.Response> getApplicationsByApplicant(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        return studyApplicationRepository.findByApplicant(user).stream()
                .map(this::toDto)
                .collect(toList());
    }

    // 내가 스터디에 신청한 현황
    public List<StudyApplicationDto.Response> getApplicationsByAuthor(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        return studyApplicationRepository.findByStudy_Author(user).stream()
                .map(this::toDto)
                .collect(toList());
    }

    @Transactional(readOnly = true)
    public List<StudyApplicationDto.Response> getApplicationsByStudyIdAndAuthor(String email, Long studyId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STUDY_NOT_FOUND));

        if (!study.getAuthor().getUserId().equals(user.getUserId())) {
            throw new BusinessException(ErrorCode.STUDY_APPLICATION_ACCESS_DENIED);
        }

        return studyApplicationRepository.findByStudy(study).stream()
                .map(this::toDto)
                .collect(toList());
    }

    // 스터디 신청 수락
    @Transactional
    public void acceptApplication(String email, Long applicationId, String openLink) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        StudyApplication app = studyApplicationRepository.findByIdAndStudy_Author(applicationId, user)
                .orElseThrow(() -> new BusinessException(ErrorCode.STUDY_APPLICATION_NOT_FOUND));

        app.setStatus(ApplicationStatus.ACCEPTED);
        app.setOpenLink(openLink);
    }

    // 스터디 신청 거절
    @Transactional
    public void rejectApplication(String email, Long applicationId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        StudyApplication app = studyApplicationRepository.findByIdAndStudy_Author(applicationId, user)
                .orElseThrow(() -> new BusinessException(ErrorCode.STUDY_APPLICATION_NOT_FOUND));

        app.setStatus(ApplicationStatus.REJECTED);
    }

    // 스터디 신청 취소
    @Transactional
    public void cancelApplication(Long applicationId, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        StudyApplication studyApplication = studyApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STUDY_APPLICATION_NOT_FOUND));

        if (!studyApplication.getApplicant().getUserId().equals(user.getUserId())) {
            throw new BusinessException(ErrorCode.STUDY_APPLICATION_CANCEL_DENIED);
        }

        studyApplicationRepository.delete(studyApplication);
    }

    private StudyApplicationDto.Response toDto(StudyApplication app) {
        return StudyApplicationDto.Response.builder()
                .applicationId(app.getId())
                .studyId(app.getStudy().getId())
                .studyTitle(app.getStudy().getTitle())
                .applicantNickname(app.getApplicant().getNickname())
                .message(app.getMessage())
                .status(app.getStatus())
                .openLink(app.getOpenLink())
                .createdAt(app.getCreatedAt())
                .build();
    }
}

package com.Buddymate.pickMate.controller;

import com.Buddymate.pickMate.config.JwtTokenProvider;
import com.Buddymate.pickMate.dto.StudyApplicationDto;
import com.Buddymate.pickMate.service.StudyApplicationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.Buddymate.pickMate.utils.JwtUtils.extractTokenFromRequest;

@RestController
@RequestMapping("/api/study-applications")
@RequiredArgsConstructor
public class StudyApplicationController {

    private final StudyApplicationService studyApplicationService;
    private final JwtTokenProvider jwtTokenProvider;

    // 스터디 신청
    @PostMapping("/{studyId}")
    public ResponseEntity<StudyApplicationDto.Response> apply(@PathVariable Long studyId,
                                                              @RequestBody StudyApplicationDto.CreateRequest createRequest,
                                                              HttpServletRequest request) {
        String email = jwtTokenProvider.getEmailFromToken(
                extractTokenFromRequest(request));

        return ResponseEntity.ok(studyApplicationService.apply(email, studyId, createRequest));
    }

    // 내가 신청한 스터디 목록
    @GetMapping("/applied")
    public ResponseEntity<List<StudyApplicationDto.Response>> getMyAppliedStudies(HttpServletRequest request) {
        String email = jwtTokenProvider.getEmailFromToken(
                extractTokenFromRequest(request));

        return ResponseEntity.ok(studyApplicationService.getApplicationsByApplicant(email));
    }

    // 내가 등록한 스터디에 들어온 신청자 목록
    @GetMapping("/received")
    public ResponseEntity<List<StudyApplicationDto.Response>> getReceivedApplications(HttpServletRequest request) {
        String email = jwtTokenProvider.getEmailFromToken(
                extractTokenFromRequest(request));

        return ResponseEntity.ok(studyApplicationService.getApplicationsByAuthor(email));
    }

    // 특정 스터디에 대한 신청자 목록
    @GetMapping("/received/{studyId}")
    public ResponseEntity<List<StudyApplicationDto.Response>> getApplicantsByStudy(@PathVariable Long studyId,
                                                                                   HttpServletRequest request) {
        String email = jwtTokenProvider.getEmailFromToken(
                extractTokenFromRequest(request));

        return ResponseEntity.ok(studyApplicationService.getApplicationsByStudyIdAndAuthor(email, studyId));
    }

    // 신청 수락
    @PostMapping("/{applicationId}/accept")
    public ResponseEntity<String> accept(@PathVariable Long applicationId,
                                         @RequestParam String openLink,
                                         HttpServletRequest request) {
        String email = jwtTokenProvider.getEmailFromToken(
                extractTokenFromRequest(request));

        studyApplicationService.acceptApplication(email, applicationId, openLink);

        return ResponseEntity.ok("신청이 수락되었습니다.");
    }

    // 신청 거절
    @PostMapping("/{applicationId}/reject")
    public ResponseEntity<String> reject(@PathVariable Long applicationId,
                                         HttpServletRequest request) {
        String email = jwtTokenProvider.getEmailFromToken(
                extractTokenFromRequest(request));

        studyApplicationService.rejectApplication(email, applicationId);

        return ResponseEntity.ok("신청이 거절되었습니다.");
    }
}

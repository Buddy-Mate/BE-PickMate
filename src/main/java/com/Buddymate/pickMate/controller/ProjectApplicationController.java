package com.Buddymate.pickMate.controller;

import com.Buddymate.pickMate.config.JwtTokenProvider;
import com.Buddymate.pickMate.dto.ProjectApplicationDto;
import com.Buddymate.pickMate.service.ProjectApplicationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.Buddymate.pickMate.utils.JwtUtils.extractTokenFromRequest;

@RestController
@RequestMapping("/api/project-applications")
@RequiredArgsConstructor
public class ProjectApplicationController {

    private final ProjectApplicationService projectApplicationService;
    private final JwtTokenProvider jwtTokenProvider;

    // 프로젝트 신청
    @PostMapping("/{projectId}")
    public ResponseEntity<ProjectApplicationDto.Response> apply(@PathVariable Long projectId,
                                                                @RequestBody ProjectApplicationDto.CreateRequest createRequest,
                                                                HttpServletRequest request) {
        String email = jwtTokenProvider.getEmailFromToken(
                extractTokenFromRequest(request));

        return ResponseEntity.ok(projectApplicationService.apply(email, projectId, createRequest));
    }

    // 특정 프로젝트에 들어온 신청자 목록 조회
    @GetMapping("/received/{projectId}")
    public ResponseEntity<List<ProjectApplicationDto.Response>> getApplicantsByProject(@PathVariable Long projectId,
                                                                                       HttpServletRequest request) {
        String email = jwtTokenProvider.getEmailFromToken(
                extractTokenFromRequest(request));

        return ResponseEntity.ok(projectApplicationService.getApplicationsByProjectIdAndAuthor(email, projectId));
    }

    // 신청 수락
    @PostMapping("/{applicationId}/accept")
    public ResponseEntity<String> acceptApplication(@PathVariable Long applicationId,
                                                    @RequestParam String openLink,
                                                    HttpServletRequest request) {
        String email = jwtTokenProvider.getEmailFromToken(
                extractTokenFromRequest(request));

        projectApplicationService.acceptApplication(email, applicationId, openLink);

        return ResponseEntity.ok("신청이 수락되었습니다.");
    }

    // 신청 거절
    @PostMapping("/{applicationId}/reject")
    public ResponseEntity<String> rejectApplication(@PathVariable Long applicationId,
                                                    HttpServletRequest request) {
        String email = jwtTokenProvider.getEmailFromToken(
                extractTokenFromRequest(request));

        projectApplicationService.rejectApplication(email, applicationId);

        return ResponseEntity.ok("신청이 거절되었습니다.");
    }
}

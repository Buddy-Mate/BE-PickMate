package com.Buddymate.pickMate.controller;

import com.Buddymate.pickMate.config.JwtTokenProvider;
import com.Buddymate.pickMate.dto.ProjectDto;
import com.Buddymate.pickMate.dto.StudyDto;
import com.Buddymate.pickMate.dto.UserResponseDto;
import com.Buddymate.pickMate.dto.UserUpdateRequestDto;
import com.Buddymate.pickMate.service.ProjectService;
import com.Buddymate.pickMate.service.StudyService;
import com.Buddymate.pickMate.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.Buddymate.pickMate.utils.JwtUtils.extractTokenFromRequest;

@Slf4j
@RestController
@RequestMapping("/api/my")
@RequiredArgsConstructor
public class UserController {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    private final ProjectService projectService;
    private final StudyService studyService;

    @GetMapping("/info")
    public ResponseEntity<?> getUserInfo(HttpServletRequest request) {
        // 요청 헤더에서 Authorization 가져오기
        String token = extractTokenFromRequest(request);

        if (token == null) {
            log.warn("Authorization 헤더가 없거나 형식이 올바르지 않음");
            return ResponseEntity.status(401).body("토큰이 필요합니다.");
        }

        // 토큰 검증
        if (!jwtTokenProvider.validateToken(token)) {
            log.warn("JWT 검증 실패: {}", token);
            return ResponseEntity.status(401).body("유효하지 않은 토큰입니다.");
        }

        // 토큰에서 이메일 추출
        String email = jwtTokenProvider.getEmailFromToken(token);

        UserResponseDto userInfo = userService.getUserByEmail(email);

        return ResponseEntity.ok(userInfo);
    }

    //한줄 소개 및 닉네임 수정
    @PutMapping("/update")
    public ResponseEntity<String> updateUser (HttpServletRequest request,
                                              @RequestBody UserUpdateRequestDto dto) {
        String email = jwtTokenProvider.getEmailFromToken(
                extractTokenFromRequest(request));

        userService.updateUser(email, dto);

        return ResponseEntity.ok("사용자 정보가 업데이트 되었습니다.");
    }

    // 내가 등록한 프로젝트 리스트
    @GetMapping("/projects")
    public ResponseEntity<List<ProjectDto.Response>> getMyProjects(HttpServletRequest request) {
        String email = jwtTokenProvider.getEmailFromToken(
                extractTokenFromRequest(request));

        return ResponseEntity.ok(projectService.getProjectsByAuthor(email));
    }

    // 내가 등록한 스터디 리스트
    @GetMapping("/studies")
    public ResponseEntity<List<StudyDto.Response>> getMyStudies(HttpServletRequest request) {
        String email = jwtTokenProvider.getEmailFromToken(
                extractTokenFromRequest(request));

        return ResponseEntity.ok(studyService.getStudiesByAuthor(email));
    }

}

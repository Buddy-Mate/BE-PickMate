package com.Buddymate.pickMate.controller;

import com.Buddymate.pickMate.config.JwtTokenProvider;
import com.Buddymate.pickMate.dto.StudyDto;
import com.Buddymate.pickMate.service.StudyService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import static com.Buddymate.pickMate.utils.JwtUtils.extractTokenFromRequest;
import java.util.List;

@RestController
@RequestMapping("/api/studies")
@RequiredArgsConstructor
public class StudyController {

    private final StudyService studyService;
    private final JwtTokenProvider jwtTokenProvider;

    // 스터디 생성
    @PostMapping("/addStudy")
    public ResponseEntity<StudyDto.Response> createStudy(HttpServletRequest request, @RequestBody StudyDto.CreateRequest createRequest) {
        String email = jwtTokenProvider.getEmailFromToken(
                extractTokenFromRequest(request));

        return ResponseEntity.ok(studyService.createStudy(email, createRequest));
    }

    // 전체 스터디 조회
    @GetMapping("/all")
    public ResponseEntity<List<StudyDto.Response>> getAllStudies() {
        return ResponseEntity.ok(studyService.getAllStudies());
    }

    // 단일 스터디 조회 (조회수 증가)
    @GetMapping("/{id}")
    public ResponseEntity<StudyDto.Response> getStudyById(@PathVariable Long id, HttpServletRequest request) {

        String token = extractTokenFromRequest(request);
        String email = null;

        if (token != null && jwtTokenProvider.validateToken(token)) {
            email = jwtTokenProvider.getEmailFromToken(token);
        }

        return ResponseEntity.ok(studyService.getStudyById(id, email));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudyDto.Response> updateStudy(HttpServletRequest request,
                                                         @PathVariable Long id,
                                                         @RequestBody StudyDto.CreateRequest updateReqeust) {
        String email = jwtTokenProvider.getEmailFromToken(
                extractTokenFromRequest(request));
        return ResponseEntity.ok(studyService.updateStudy(email, id ,updateReqeust));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteStudy(HttpServletRequest request, @PathVariable Long id) {
        String email = jwtTokenProvider.getEmailFromToken(
                extractTokenFromRequest(request));
        studyService.deleteStudy(email, id);
        return ResponseEntity.ok("스터디 게시글이 삭제되었습니다.");
    }

    // 스터디 좋아요
    @PostMapping("/{id}/like")
    public ResponseEntity<String> likeStudy(@PathVariable Long id,
                                            HttpServletRequest request) {

        String email = jwtTokenProvider.getEmailFromToken(
                extractTokenFromRequest(request));

        studyService.likeStudy(email, id);

        return ResponseEntity.ok("이 스터디 게시글을 좋아합니다.");
    }

    // 스터디 좋아요 취소
    @DeleteMapping("{id}/like")
    public ResponseEntity<String> unlikeStudy(@PathVariable Long id,
                                              HttpServletRequest request) {

        String email = jwtTokenProvider.getEmailFromToken(
                extractTokenFromRequest(request));

        studyService.unlikeStudy(email, id);

        return ResponseEntity.ok("스터디 게시글 좋아요를 취소합니다.");
    }
}

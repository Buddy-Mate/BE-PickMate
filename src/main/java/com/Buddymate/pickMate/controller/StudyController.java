package com.Buddymate.pickMate.controller;

import com.Buddymate.pickMate.config.JwtTokenProvider;
import com.Buddymate.pickMate.dto.StudyDto;
import com.Buddymate.pickMate.service.StudyService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/studies")
@RequiredArgsConstructor
public class StudyController {

    private final StudyService studyService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/addStudy")
    public ResponseEntity<StudyDto.Response> createStudy(HttpServletRequest request, @RequestBody StudyDto.CreateRequest createRequest) {
        String email = jwtTokenProvider.getEmailFromToken(extractTokenFromRequest(request));

        return ResponseEntity.ok(studyService.createStudy(email, createRequest));
    }

    @GetMapping("/all")
    public ResponseEntity<List<StudyDto.Response>> getAllStudies() {
        return ResponseEntity.ok(studyService.getAllStudies());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudyDto.Response> getStudyById(@PathVariable Long id) {
        return ResponseEntity.ok(studyService.getStudyById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudyDto.Response> updateStudy(HttpServletRequest request,
                                                         @PathVariable Long id,
                                                         @RequestBody StudyDto.CreateRequest updateReqeust) {
        String email = jwtTokenProvider.getEmailFromToken(extractTokenFromRequest(request));
        return ResponseEntity.ok(studyService.updateStudy(email, id ,updateReqeust));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteStudy(HttpServletRequest request, @PathVariable Long id) {
        String email = jwtTokenProvider.getEmailFromToken(extractTokenFromRequest(request));
        studyService.deleteStudy(email, id);
        return ResponseEntity.ok("스터디 게시글이 삭제되었습니다.");
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        return (bearerToken != null && bearerToken.startsWith("Bearer ")) ? bearerToken.substring(7) : null;
    }
}

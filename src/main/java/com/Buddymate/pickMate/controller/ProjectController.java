package com.Buddymate.pickMate.controller;

import com.Buddymate.pickMate.config.JwtTokenProvider;
import com.Buddymate.pickMate.dto.ProjectDto;
import com.Buddymate.pickMate.service.ProjectService;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    @Autowired
    private final ProjectService projectService;
    private final JwtTokenProvider jwtTokenProvider;

    // 프로젝트 생성
    @PostMapping("/addProject")
    public ResponseEntity<ProjectDto.Response> createProject(HttpServletRequest request, @RequestBody ProjectDto.CreateRequest createRequest) {
        String token = extractTokenFromRequest(request);

        // 이메일 추출
        String email = jwtTokenProvider.getEmailFromToken(token);
        return ResponseEntity.ok(projectService.createProject(email, createRequest));
    }

    // 전체 프로젝트 조회
    @GetMapping("/all")
    public ResponseEntity<List<ProjectDto.Response>> getAllProjects() {
        return ResponseEntity.ok(projectService.getAllProjects());
    }

    // 단일 프로젝트 조회 (조회수 증가)
    @GetMapping("/{id}")
    public ResponseEntity<ProjectDto.Response> getProjectById(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.getProjectById(id));
    }

    // 프로젝트 수정
    @PutMapping("/{id}")
    public ResponseEntity<ProjectDto.Response> updateProject(HttpServletRequest request,
                                                             @PathVariable Long id,
                                                             @RequestBody ProjectDto.CreateRequest updateRequest) {
        String token = extractTokenFromRequest(request);

        String email = jwtTokenProvider.getEmailFromToken(token);
        return ResponseEntity.ok(projectService.updateProject(email, id, updateRequest));
    }

    // 프로젝트 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(HttpServletRequest request, @PathVariable Long id) {
        String token = extractTokenFromRequest(request);
        String email = jwtTokenProvider.getEmailFromToken(token);
        projectService.deleteProject(email, id);
        return ResponseEntity.ok().build();
    }

    // 요청 헤더에서 Token 추출
    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }
}

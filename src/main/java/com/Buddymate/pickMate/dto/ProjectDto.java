package com.Buddymate.pickMate.dto;

import com.Buddymate.pickMate.entity.Project;
import com.Buddymate.pickMate.enums.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ProjectDto {

    // 프로젝트 생성 요청 DTO
    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRequest {
        private String title;
        private String description;
        private List<String> techStack;
        private LocalDateTime deadline;
    }

    // 프로젝트 응답 DTO (조회)
    @Getter @Setter
    public static class Response {
        private Long id;
        private String title;
        private String description;
        private List<String> techStack;
        private String authorNickname;
        private int likes;
        private int views;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private LocalDateTime deadline;
        private ApplicationStatus applicationStatus;

        public Response(Project project) {
            this.id = project.getId();
            this.title = project.getTitle();
            this.description = project.getDescription();
            this.techStack = convertStringToList(project.getTechStack());
            this.authorNickname = project.getAuthor().getNickname();
            this.likes = project.getLikes();
            this.views = project.getViews();
            this.createdAt = project.getCreatedAt();
            this.updatedAt = project.getUpdatedAt();
            this.deadline = project.getDeadline();
        }
    }

    public static String convertListToString(List<String> techStack) {
        return techStack != null ? String.join(",", techStack) : null;
    }

    public static List<String> convertStringToList(String techStackStr) {
        return techStackStr != null && !techStackStr.isBlank()
                ? Arrays.stream(techStackStr.split(","))
                .map(String::trim)
                .collect(Collectors.toList())
                : new ArrayList<>();
    }
}

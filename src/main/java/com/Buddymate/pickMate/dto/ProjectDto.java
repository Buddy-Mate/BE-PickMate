package com.Buddymate.pickMate.dto;

import com.Buddymate.pickMate.entity.Project;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

public class ProjectDto {

    // 프로젝트 생성 요청 DTO
    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRequest {
        private String title;
        private String description;
        private String techStack;
        private LocalDateTime deadline;
    }

    // 프로젝트 응답 DTO (조회)
    @Getter @Setter
    public static class Response {
        private Long id;
        private String title;
        private String description;
        private String techStack;
        private String authorNickname;
        private int likes;
        private int views;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private LocalDateTime deadline;

        public Response(Project project) {
            this.id = project.getId();
            this.title = project.getTitle();
            this.description = project.getDescription();
            this.techStack = project.getTechStack();
            this.authorNickname = project.getAuthor().getNickname();
            this.likes = project.getLikes();
            this.views = project.getViews();
            this.createdAt = project.getCreatedAt();
            this.updatedAt = project.getUpdatedAt();
            this.deadline = project.getDeadline();
        }
    }
}

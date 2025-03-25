package com.Buddymate.pickMate.dto;

import com.Buddymate.pickMate.entity.Study;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

public class StudyDto {

    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRequest {
        private String title;
        private String description;
        private LocalDateTime deadline;
    }

    @Getter
    public static class Response {
        private Long id;
        private String title;
        private String description;
        private String authorNickname;
        private int likes;
        private int views;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private LocalDateTime deadline;

        public Response(Study study) {
            this.id = study.getId();
            this.title = study.getTitle();
            this.description = study.getDescription();
            this.authorNickname = study.getAuthor().getNickname();
            this.likes = study.getLikes();
            this.views = study.getViews();
            this.createdAt = study.getCreatedAt();
            this.updatedAt = study.getUpdatedAt();
            this.deadline = study.getDeadline();
        }
    }
}

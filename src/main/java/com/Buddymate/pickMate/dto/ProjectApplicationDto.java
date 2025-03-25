package com.Buddymate.pickMate.dto;

import com.Buddymate.pickMate.enums.ApplicationStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

public class ProjectApplicationDto {

    @Getter @Setter
    public static class CreateRequest {
        private String message;
    }

    @Getter @Setter
    @Builder
    public static class Response {
        private Long applicationId;
        private String projectTitle;
        private String applicantNickname;
        private String message;
        private ApplicationStatus status;
        private String openLink;
        private LocalDateTime createdAt;
    }
}

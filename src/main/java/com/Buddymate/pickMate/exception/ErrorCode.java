package com.Buddymate.pickMate.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    FILE_CONVERT_FAIL("FILE_001", "파일 변환에 실패했습니다."),
    INVALID_FILE_TYPE("FILE_002", "지원하지 않는 파일 형식입니다."),
    FILE_UPLOAD_FAIL("FILE_003", "파일 업로드에 실패했습니다."),
    USER_NOT_FOUND("USER_001", "해당 유저를 찾을 수 없습니다."),
    DUPLICATE_NICKNAME("USER_002", "이미 사용 중인 닉네임입니다."),
    INVALID_ACCESS("ACCESS_001", "접근 권한이 없습니다."),
    PROJECT_NOT_FOUND("PROJECT_001", "해당 프로젝트를 찾을 수 없습니다."),
    STUDY_NOT_FOUND("STUDY_001", "해당 스터디를 찾을 수 없습니다.");

    private final String code;
    private final String message;
}

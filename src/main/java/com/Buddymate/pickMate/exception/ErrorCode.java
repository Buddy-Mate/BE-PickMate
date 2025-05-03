package com.Buddymate.pickMate.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    LOGIN_INVALID_EMAIL("AUTH_001", "존재하지 않는 이메일입니다."),
    LOGIN_INVALID_PASSWORD("AUTH_002", "비밀번호가 올바르지 않습니다."),
    FILE_CONVERT_FAIL("FILE_001", "파일 변환에 실패했습니다."),
    INVALID_FILE_TYPE("FILE_002", "지원하지 않는 파일 형식입니다."),
    FILE_UPLOAD_FAIL("FILE_003", "파일 업로드에 실패했습니다."),
    USER_NOT_FOUND("USER_001", "해당 유저를 찾을 수 없습니다."),
    DUPLICATE_NICKNAME("USER_002", "이미 사용 중인 닉네임입니다."),
    DUPLICATE_EMAIL("USER_003", "이미 존재하는 이메일입니다."),
    INVALID_ACCESS("ACCESS_001", "접근 권한이 없습니다."),
    PROJECT_NOT_FOUND("PROJECT_001", "해당 프로젝트를 찾을 수 없습니다."),
    PROJECT_INVALID_UPDATE("PROJECT_002", "프로젝트 수정 권한이 없습니다."),
    PROJECT_INVALID_DELETE("PROJECT_003", "프로젝트 삭제 권한이 없습니다."),
    PROJECT_LIKE_DUPLICATE("PROJECT_004", "이미 좋아요를 눌렀습니다."),
    PROJECT_LIKE_NOT_FOUND("PROJECT_005", "좋아요를 누르지 않았습니다."),
    PROJECT_SELF_APPLICATION_NOT_ALLOWED("PROJECT_006", "자신의 프로젝트에는 신청할 수 없습니다."),
    PROJECT_APPLICATION_NOT_FOUND("PROJECT_007", "프로젝트 신청 정보를 찾을 수 없습니다."),
    PROJECT_APPLICATION_ACCESS_DENIED("PROJECT_008", "신청자 목록을 조회할 권한이 없습니다."),
    PROJECT_APPLICATION_CANCEL_DENIED("PROJECT_009", "본인 신청만 취소할 수 있습니다."),
    STUDY_NOT_FOUND("STUDY_001", "해당 스터디를 찾을 수 없습니다."),
    STUDY_INVALID_UPDATE("STUDY_002", "스터디 수정 권한이 없습니다."),
    STUDY_INVALID_DELETE("STUDY_003", "스터디 삭제 권한이 없습니다."),
    STUDY_LIKE_DUPLICATE("STUDY_004", "이미 좋아요를 눌렀습니다."),
    STUDY_LIKE_NOT_FOUND("STUDY_005", "좋아요를 누르지 않았습니다."),
    STUDY_SELF_APPLICATION_NOT_ALLOWED("STUDY_006", "자신의 스터디에는 신청할 수 없습니다."),
    STUDY_APPLICATION_NOT_FOUND("STUDY_007", "스터디 신청 정보를 찾을 수 없습니다."),
    STUDY_APPLICATION_ACCESS_DENIED("STUDY_008", "신청자 목록을 조회할 권한이 없습니다."),
    STUDY_APPLICATION_CANCEL_DENIED("STUDY_009", "본인 신청만 취소할 수 있습니다.");


    private final String code;
    private final String message;
}

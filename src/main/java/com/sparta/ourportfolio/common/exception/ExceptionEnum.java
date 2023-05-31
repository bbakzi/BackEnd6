package com.sparta.ourportfolio.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ExceptionEnum {
    // 400 Bad Request
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "400", "아이디 또는 비밀번호가 일치하지 않습니다."),
    PASSWORD_REGEX(HttpStatus.BAD_REQUEST, "400", "비밀번호는 8~15자리, 영어, 숫자 조합으로 구성되어야 합니다."),
    NICKNAME_REGEX(HttpStatus.BAD_REQUEST, "400", "닉네임은 닉네임은 10자리, 영어, 한글, 숫자 조합으로 구성되어야합니다."),
    USER_INFORMATION(HttpStatus.BAD_REQUEST, "400", "변경할 회원정보를 작성하지 않았습니다."),
    COINCIDE_PASSWORD(HttpStatus.BAD_REQUEST, "400", "새로운 비밀번호와 확인 비밀번호가 일치하지 않습니다."),

    // 401 Unauthorized
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "401", "권한이 없습니다."),

    // 404 Not Found
    NOT_FOUND_POST(HttpStatus.NOT_FOUND, "404_1", "게시글이 존재하지 않습니다."),
    NOT_FOUND_COMMENT(HttpStatus.NOT_FOUND, "404_2", "댓글이 존재하지 않습니다."),
    NOT_FOUND_USER(HttpStatus.NOT_FOUND, "404_3", "회원이 존재하지 않습니다."),
    NOT_FOUND_PROJECT(HttpStatus.NOT_FOUND, "404", "프로젝트가 존재하지 않습니다."),
    NOT_FOUND_PORTFOLIO(HttpStatus.NOT_FOUND,"404","포트폴리오가 존재하지 않습니다."),

    // 409 Conflict
    DUPLICATED_USER_NAME(HttpStatus.CONFLICT, "409", "중복된 아이디가 이미 존재합니다."),
    DUPLICATED_NICK_NAME(HttpStatus.CONFLICT, "409", "중복된 닉네임이 이미 존재합니다."),
    EXISTED_NICK_NAME(HttpStatus.CONFLICT, "409", "기존 닉네임과 동일합니다."),
    USER_IS_DELETED(HttpStatus.CONFLICT, "409", "회원 탈퇴가 된 상태입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    ExceptionEnum(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
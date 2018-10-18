package com.cyzest.cyrello.exception;

import io.github.cyzest.commons.spring.model.ExceptionType;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum CommonExceptionType implements ExceptionType {

    // 잘못된 요청
    BAD_REQUEST(400, "Bad Request", HttpStatus.BAD_REQUEST),

    // 인증 실패
    UNAUTHORIZED(401, "Unauthorized", HttpStatus.UNAUTHORIZED),

    // 접근 거부
    FORBIDDEN(403, "Forbidden", HttpStatus.FORBIDDEN),

    // 리소스 없음
    NOT_FOUND(404, "Not Found", HttpStatus.NOT_FOUND);

    private final int resultCode;
    private final String resultMessage;
    private final HttpStatus statusCode;

    CommonExceptionType(int resultCode, String resultMessage, HttpStatus statusCode) {
        this.resultCode = resultCode;
        this.resultMessage = resultMessage;
        this.statusCode = statusCode;
    }

}

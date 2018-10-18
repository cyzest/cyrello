package com.cyzest.cyrello.exception;

import io.github.cyzest.commons.spring.model.ExceptionType;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum UserExceptionType implements ExceptionType {

    // 존재하는 사용자
    EXIST_USER(4001, "Exist User", HttpStatus.BAD_REQUEST);

    private final int resultCode;
    private final String resultMessage;
    private final HttpStatus statusCode;

    UserExceptionType(int resultCode, String resultMessage, HttpStatus statusCode) {
        this.resultCode = resultCode;
        this.resultMessage = resultMessage;
        this.statusCode = statusCode;
    }

}

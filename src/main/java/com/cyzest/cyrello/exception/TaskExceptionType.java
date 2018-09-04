package com.cyzest.cyrello.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum TaskExceptionType implements ExceptionType {

    // 유효하지 않은 참조 태스크 포함
    CONTAINS_INVALID_REL_TASK(4101, "Contains Invalid Relation Task", HttpStatus.BAD_REQUEST),

    // 완료처리 된 참조 태스크 포함
    CONTAINS_COMPLETED_REL_TASK(4102, "Contains Completed Relation Task", HttpStatus.BAD_REQUEST);

    private final int resultCode;
    private final String resultMessage;
    private final HttpStatus statusCode;

    TaskExceptionType(int resultCode, String resultMessage, HttpStatus statusCode) {
        this.resultCode = resultCode;
        this.resultMessage = resultMessage;
        this.statusCode = statusCode;
    }

}
